package com.kieronquinn.app.classicpowermenu.components.quickaccesswallet

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.service.quickaccesswallet.GetWalletCardsRequest
import android.service.quickaccesswallet.QuickAccessWalletClient
import android.service.quickaccesswallet.CPMQuickAccessWalletClientImpl
import android.service.quickaccesswallet.SelectWalletCardRequest
import com.ironz.unsafe.UnsafeAndroid
import java.util.*
import java.util.concurrent.Executor

open class CPMQuickAccessWalletClientImpl(context: Context): QuickAccessWalletClient, ServiceConnection {

    private val quickAccessWalletClient by lazy {
        UnsafeAndroid().allocateInstance(CPMQuickAccessWalletClientImpl::class.java).apply {
            setField("mContext", context.applicationContext)
            setField("mServiceInfo", CPMQuickAccessWalletServiceInfoBuilder.create(context))
            setField("mHandler", Handler(Looper.getMainLooper()))
            setField("mRequestQueue", LinkedList<Any>())
            setField("mEventListeners", HashMap<Any, Any>(1))
            setField("mIsConnected", true)
        }
    }

    private fun CPMQuickAccessWalletClientImpl.setField(fieldName: String, value: Any?){
        CPMQuickAccessWalletClientImpl::class.java.getDeclaredField(fieldName).apply {
            isAccessible = true
        }.set(this, value)
    }

    override fun close() {
        quickAccessWalletClient.close()
    }

    override fun isWalletServiceAvailable(): Boolean {
        return quickAccessWalletClient.isWalletServiceAvailable
    }

    override fun isWalletFeatureAvailable(): Boolean {
        return quickAccessWalletClient.isWalletFeatureAvailable
    }

    override fun isWalletFeatureAvailableWhenDeviceLocked(): Boolean {
        return quickAccessWalletClient.isWalletFeatureAvailableWhenDeviceLocked
    }

    override fun getWalletCards(
        request: GetWalletCardsRequest,
        callback: QuickAccessWalletClient.OnWalletCardsRetrievedCallback
    ) {
        return quickAccessWalletClient.getWalletCards(request, callback)
    }

    override fun getWalletCards(
        executor: Executor,
        request: GetWalletCardsRequest,
        callback: QuickAccessWalletClient.OnWalletCardsRetrievedCallback
    ) {
        return quickAccessWalletClient.getWalletCards(executor, request, callback)
    }

    override fun selectWalletCard(request: SelectWalletCardRequest) {
        return quickAccessWalletClient.selectWalletCard(request)
    }

    override fun notifyWalletDismissed() {
        return quickAccessWalletClient.notifyWalletDismissed()
    }

    override fun addWalletServiceEventListener(listener: QuickAccessWalletClient.WalletServiceEventListener) {
        return quickAccessWalletClient.addWalletServiceEventListener(listener)
    }

    override fun addWalletServiceEventListener(
        executor: Executor,
        listener: QuickAccessWalletClient.WalletServiceEventListener
    ) {
        return addWalletServiceEventListener(executor, listener)
    }

    override fun removeWalletServiceEventListener(listener: QuickAccessWalletClient.WalletServiceEventListener) {
        return quickAccessWalletClient.removeWalletServiceEventListener(listener)
    }

    override fun disconnect() {
        return quickAccessWalletClient.disconnect()
    }

    override fun createWalletIntent(): Intent? {
        return quickAccessWalletClient.createWalletIntent()
    }

    override fun createWalletSettingsIntent(): Intent? {
        return quickAccessWalletClient.createWalletSettingsIntent()
    }

    override fun getLogo(): Drawable? {
        return quickAccessWalletClient.logo
    }

    override fun getServiceLabel(): CharSequence? {
        return quickAccessWalletClient.serviceLabel
    }

    override fun getShortcutShortLabel(): CharSequence? {
        return quickAccessWalletClient.shortcutShortLabel
    }

    override fun getShortcutLongLabel(): CharSequence? {
        return quickAccessWalletClient.shortcutLongLabel
    }

    override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
        quickAccessWalletClient.onServiceConnected(p0, p1)
    }

    override fun onServiceDisconnected(p0: ComponentName?) {
        quickAccessWalletClient.onServiceDisconnected(p0)
    }

}