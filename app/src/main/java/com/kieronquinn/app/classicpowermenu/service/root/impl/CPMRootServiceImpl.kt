package com.kieronquinn.app.classicpowermenu.service.root.impl

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.ActivityOptions
import android.app.IActivityManager
import android.app.PendingIntent
import android.app.trust.ITrustManager
import android.content.Context
import android.content.Intent
import android.hardware.input.IInputManager
import android.os.*
import android.util.Log
import android.view.IWindowManager
import android.view.InputDevice
import android.view.KeyCharacterMap
import android.view.KeyEvent
import com.android.internal.widget.ILockSettings
import com.android.systemui.util.extensions.UserHandle_USER_ALL
import com.android.systemui.util.extensions.mTarget
import com.google.common.hash.Hashing
import com.kieronquinn.app.classicpowermenu.BuildConfig
import com.kieronquinn.app.classicpowermenu.IClassicPowerMenu
import com.kieronquinn.app.classicpowermenu.components.quickaccesswallet.GooglePayConstants
import com.kieronquinn.app.classicpowermenu.model.service.ActivityContainer
import com.kieronquinn.app.classicpowermenu.model.service.BroadcastContainer
import com.kieronquinn.app.classicpowermenu.model.service.ServiceBindContainer
import com.kieronquinn.app.classicpowermenu.model.service.ServiceUnbindContainer
import com.kieronquinn.app.classicpowermenu.utils.extensions.broadcastIntentWithFeatureCompat
import com.topjohnwu.superuser.internal.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import rikka.shizuku.SystemServiceHelper
import java.io.File

@SuppressLint("RestrictedApi", "BlockedPrivateApi")
class CPMRootServiceImpl: IClassicPowerMenu.Stub() {

    private val activityManager by lazy {
        val activityManagerProxy = SystemServiceHelper.getSystemService("activity")
        IActivityManager.Stub.asInterface(activityManagerProxy)
    }

    private val inputManager by lazy {
        val inputManagerProxy = SystemServiceHelper.getSystemService("input")
        IInputManager.Stub.asInterface(inputManagerProxy)
    }

    private val lockSettings by lazy {
        val lockSettingsProxy = SystemServiceHelper.getSystemService("lock_settings")
        ILockSettings.Stub.asInterface(lockSettingsProxy)
    }

    private val windowManager by lazy {
        val windowManagerProxy = SystemServiceHelper.getSystemService("window")
        IWindowManager.Stub.asInterface(windowManagerProxy)
    }

    private val userManager by lazy {
        val userManagerProxy = SystemServiceHelper.getSystemService("user")
        IUserManager.Stub.asInterface(userManagerProxy)
    }

    private val trustManager by lazy {
        val trustManagerProxy = SystemServiceHelper.getSystemService("trust")
        ITrustManager.Stub.asInterface(trustManagerProxy)
    }

    private val powerManager by lazy {
        val powerManagerProxy = SystemServiceHelper.getSystemService("power")
        IPowerManager.Stub.asInterface(powerManagerProxy)
    }

    private val context by lazy {
        Utils.getContext().createPackageContext(BuildConfig.APPLICATION_ID, Context.CONTEXT_IGNORE_SECURITY or Context.CONTEXT_INCLUDE_CODE)
    }

    override fun bindServicePrivileged(bindContainer: ServiceBindContainer, intent: Intent, flags: Int): Int {
        try {
            Context::class.java.getMethod("getOpPackageName").invoke(context) as String
            val userHandle = Context::class.java.getMethod("getUser").invoke(context) as UserHandle
            val identifier =
                UserHandle::class.java.getMethod("getIdentifier").invoke(userHandle) as Int
            Intent::class.java.getMethod("prepareToLeaveProcess", Context::class.java)
                .invoke(intent, context)
            return activityManager.bindServiceInstance(
                bindContainer.thread,
                bindContainer.activityToken,
                intent,
                null,
                bindContainer.serviceConnection,
                flags,
                null,
                BuildConfig.APPLICATION_ID,
                identifier
            )
        } catch (e: Exception) {
            Log.e("CPM", "Error binding service", e)
            return 0
        }
    }

    override fun unBindServicePrivileged(unbindContainer: ServiceUnbindContainer): Boolean {
        return activityManager.unbindService(unbindContainer.serviceConnection)
    }

    override fun sendBroadcastPrivileged(broadcastContainer: BroadcastContainer, intent: Intent, intentType: String?, attributionTag: String?) {
        val userHandle = Context::class.java.getMethod("getUser").invoke(context) as UserHandle
        val identifier = UserHandle::class.java.getMethod("getIdentifier").invoke(userHandle) as Int
        Intent::class.java.getMethod("prepareToLeaveProcess", Context::class.java).invoke(intent, context)
        activityManager.broadcastIntentWithFeatureCompat(broadcastContainer.thread, attributionTag, intent, intentType, identifier)
    }

    override fun getIntentForPendingIntent(pendingIntent: PendingIntent): Intent {
        return activityManager.getIntentForIntentSender(pendingIntent.mTarget)
    }

    override fun startActivityPrivileged(activityContainer: ActivityContainer, intent: Intent): Int {
        return activityManager.startActivityWithFeature(activityContainer.thread, context.packageName, null,
            intent, intent.type, null, null, 0, 0, null, ActivityOptions.makeBasic().toBundle())
    }

    override fun shutdown() {
        powerManager.shutdown(false, "", false)
    }

    override fun reboot(safeMode: Boolean) {
        if(safeMode) {
            powerManager.rebootSafeMode(false, false)
        }else{
            powerManager.reboot(false, "", false)
        }
    }

    override fun rebootWithReason(reason: String) {
        powerManager.reboot(false, reason, false)
    }

    override fun restartSystemUi() {
        Runtime.getRuntime().exec("pkill systemui")
    }

    override fun takeScreenshot() {
        //Wait for app to close
        Handler(Looper.getMainLooper()).postDelayed({
            // 2 = INJECT_INPUT_EVENT_MODE_WAIT_FOR_FINISH
            sendKeyEvent(KeyEvent.KEYCODE_SYSRQ, false)
        }, 500L)
    }

    override fun lockdown() {
        lockSettings.requireStrongAuth(0x20, UserHandle_USER_ALL)
        lockProfiles()
    }

    override fun getStrongAuth(): Int {
        val userHandle = Context::class.java.getMethod("getUser").invoke(context) as UserHandle
        val identifier = UserHandle::class.java.getMethod("getIdentifier").invoke(userHandle) as Int
        return lockSettings.getStrongAuthForUser(identifier)
    }

    override fun getGooglePayDatabaseForLoyalty(): ParcelFileDescriptor? {
        try {
            val googlePayContext = getGooglePayContext()
            val googlePayAccountId = getGooglePayCurrentAccountId(googlePayContext) ?: return null
            val hashedAccountId = Hashing.md5().hashUnencodedChars(googlePayAccountId)
            val database = googlePayContext.getDatabasePath("${hashedAccountId}_tapandpay.db")
            if (!database.exists()) return null
            return ParcelFileDescriptor.open(database, ParcelFileDescriptor.MODE_READ_ONLY)
        }catch (e: Exception){
            Log.e("CPM", "Failed to get Pay database", e)
            return null
        }
    }

    override fun getGooglePayLoyaltyImageForId(id: String): ParcelFileDescriptor? {
        try {
            val googlePayContext = getGooglePayContext()
            val googlePayAccountId = getGooglePayCurrentAccountId(googlePayContext) ?: return null
            val hashedAccountId = Hashing.md5().hashString(googlePayAccountId, Charsets.UTF_8).toString()
            val valuablesDir = File(googlePayContext.filesDir, "valuables")
            if (!valuablesDir.exists()) return null
            val imagesDir = File(valuablesDir, "images")
            if (!imagesDir.exists()) return null
            val userImagesDir = File(imagesDir, hashedAccountId)
            if (!userImagesDir.exists()) return null
            val valuableImage = File(userImagesDir, id)
            if (!valuableImage.exists()) return null
            return ParcelFileDescriptor.open(valuableImage, ParcelFileDescriptor.MODE_READ_ONLY)
        }catch (e: Exception){
            Log.e("CPM", "Failed to get image for $id", e)
            return null
        }
    }

    override fun getCurrentUser(): Int {
        return ActivityManager::class.java.getMethod("getCurrentUser").invoke(null) as Int
    }

    override fun showPowerMenu() {
        sendKeyEvent(KeyEvent.KEYCODE_POWER, true)
    }

    private fun sendKeyEvent(keyCode: Int, longpress: Boolean) {
        // 2 = INJECT_INPUT_EVENT_MODE_WAIT_FOR_FINISH
        val mode = 2
        val now = SystemClock.uptimeMillis()
        var repeatCount = 0
        val event = KeyEvent(
            now, now, KeyEvent.ACTION_DOWN, keyCode, repeatCount,
            0 /*metaState*/, KeyCharacterMap.VIRTUAL_KEYBOARD, 0 /*scancode*/, 0 /*flags*/,
            InputDevice.SOURCE_TRACKBALL
        )
        inputManager.injectInputEvent(event, mode)
        if (longpress) {
            repeatCount++
            inputManager.injectInputEvent(
                KeyEvent.changeTimeRepeat(
                    event, now, repeatCount,
                    KeyEvent.FLAG_LONG_PRESS
                ),
                mode
            )
        }
        inputManager.injectInputEvent(KeyEvent.changeAction(event, KeyEvent.ACTION_UP), mode)
    }

    private fun getGooglePayContext(): Context {
        return context.createPackageContext(GooglePayConstants.WALLET_NFC_REL_PACKAGE_NAME, Context.CONTEXT_IGNORE_SECURITY)
    }

    private fun getGooglePayCurrentAccountId(googlePayContext: Context = getGooglePayContext()): String? {
        return googlePayContext.getSharedPreferences(GooglePayConstants.WALLET_PREF_NAME, Context.MODE_PRIVATE)
            .getString(GooglePayConstants.WALLET_CURRENT_ACCOUNT_ID_PREF_KEY, null)
    }

    private fun lockProfiles() = GlobalScope.launch(Dispatchers.IO) {
        delay(300L)
        windowManager.lockNow(null)
        val userHandle = Context::class.java.getMethod("getUser").invoke(context) as UserHandle
        val identifier = UserHandle::class.java.getMethod("getIdentifier").invoke(userHandle) as Int
        val profileIds = userManager.getProfileIds(identifier, true)
        profileIds.forEach {
            trustManager.setDeviceLockedForUser(it, true)
        }
    }

    override fun resumeAppSwitches() {
        activityManager.resumeAppSwitches()
    }

}