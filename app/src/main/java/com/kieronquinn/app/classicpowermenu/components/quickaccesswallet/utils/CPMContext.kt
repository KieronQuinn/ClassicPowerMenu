package com.kieronquinn.app.classicpowermenu.components.quickaccesswallet.utils

import android.content.*
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.res.AssetManager
import android.content.res.Configuration
import android.content.res.Resources
import android.database.DatabaseErrorHandler
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.UserHandle
import android.view.Display
import androidx.annotation.RequiresPermission
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream

class CPMContext(private val originalContext: Context): Context() {

    override fun getAssets(): AssetManager {
        return originalContext.assets
    }

    override fun getResources(): Resources {
        return originalContext.resources
    }

    override fun getPackageManager(): PackageManager {
        return originalContext.packageManager
    }

    override fun getContentResolver(): ContentResolver {
        return originalContext.contentResolver
    }

    override fun getMainLooper(): Looper {
        return originalContext.mainLooper
    }

    override fun getApplicationContext(): Context {
        return originalContext.applicationContext
    }

    override fun setTheme(p0: Int) {
        originalContext.setTheme(p0)
    }

    override fun getTheme(): Resources.Theme {
        return originalContext.theme
    }

    override fun getClassLoader(): ClassLoader {
        return originalContext.classLoader
    }

    override fun getPackageName(): String {
        return originalContext.packageName
    }

    override fun getApplicationInfo(): ApplicationInfo {
        return originalContext.applicationInfo
    }

    override fun getPackageResourcePath(): String {
        return originalContext.packageResourcePath
    }

    override fun getPackageCodePath(): String {
        return originalContext.packageCodePath
    }

    override fun getSharedPreferences(p0: String?, p1: Int): SharedPreferences {
        return originalContext.getSharedPreferences(p0, p1)
    }

    override fun moveSharedPreferencesFrom(p0: Context?, p1: String?): Boolean {
        return originalContext.moveSharedPreferencesFrom(p0, p1)
    }

    override fun deleteSharedPreferences(p0: String?): Boolean {
        return originalContext.deleteSharedPreferences(p0)
    }

    override fun openFileInput(p0: String?): FileInputStream {
        return originalContext.openFileInput(p0)
    }

    override fun openFileOutput(p0: String?, p1: Int): FileOutputStream {
        return originalContext.openFileOutput(p0, p1)
    }

    override fun deleteFile(p0: String?): Boolean {
        return originalContext.deleteFile(p0)
    }

    override fun getFileStreamPath(p0: String?): File {
        return originalContext.getFileStreamPath(p0)
    }

    override fun getDataDir(): File {
        return originalContext.dataDir
    }

    override fun getFilesDir(): File {
        return originalContext.filesDir
    }

    override fun getNoBackupFilesDir(): File {
        return originalContext.noBackupFilesDir
    }

    override fun getExternalFilesDir(p0: String?): File? {
        return originalContext.getExternalFilesDir(p0)
    }

    override fun getExternalFilesDirs(p0: String?): Array<File> {
        return originalContext.getExternalFilesDirs(p0)
    }

    override fun getObbDir(): File {
        return originalContext.obbDir
    }

    override fun getObbDirs(): Array<File> {
        return originalContext.obbDirs
    }

    override fun getCacheDir(): File {
        return originalContext.cacheDir
    }

    override fun getCodeCacheDir(): File {
        return originalContext.codeCacheDir
    }

    override fun getExternalCacheDir(): File? {
        return originalContext.externalCacheDir
    }

    override fun getExternalCacheDirs(): Array<File> {
        return originalContext.externalCacheDirs
    }

    @Deprecated("Deprecated in Java")
    override fun getExternalMediaDirs(): Array<File> {
        return originalContext.externalMediaDirs
    }

    override fun fileList(): Array<String> {
        return originalContext.fileList()
    }

    override fun getDir(p0: String?, p1: Int): File {
        return originalContext.getDir(p0, p1)
    }

    override fun openOrCreateDatabase(
        p0: String?,
        p1: Int,
        p2: SQLiteDatabase.CursorFactory?
    ): SQLiteDatabase {
        return originalContext.openOrCreateDatabase(p0, p1, p2)
    }

    override fun openOrCreateDatabase(
        p0: String?,
        p1: Int,
        p2: SQLiteDatabase.CursorFactory?,
        p3: DatabaseErrorHandler?
    ): SQLiteDatabase {
        return originalContext.openOrCreateDatabase(p0, p1, p2, p3)
    }

    override fun moveDatabaseFrom(p0: Context?, p1: String?): Boolean {
        return originalContext.moveDatabaseFrom(p0, p1)
    }

    override fun deleteDatabase(p0: String?): Boolean {
        return originalContext.deleteDatabase(p0)
    }

    override fun getDatabasePath(p0: String?): File {
        return originalContext.getDatabasePath(p0)
    }

    override fun databaseList(): Array<String> {
        return originalContext.databaseList()
    }

    @Deprecated("Deprecated in Java")
    override fun getWallpaper(): Drawable {
        return originalContext.wallpaper
    }

    @Deprecated("Deprecated in Java")
    override fun peekWallpaper(): Drawable {
        return originalContext.peekWallpaper()
    }

    @Deprecated("Deprecated in Java")
    override fun getWallpaperDesiredMinimumWidth(): Int {
        return originalContext.wallpaperDesiredMinimumWidth
    }

    @Deprecated("Deprecated in Java")
    override fun getWallpaperDesiredMinimumHeight(): Int {
        return originalContext.wallpaperDesiredMinimumHeight
    }

    @Deprecated("Deprecated in Java")
    override fun setWallpaper(p0: Bitmap?) {
        return originalContext.setWallpaper(p0)
    }

    @Deprecated("Deprecated in Java")
    override fun setWallpaper(p0: InputStream?) {
        return originalContext.setWallpaper(p0)
    }

    @Deprecated("Deprecated in Java")
    override fun clearWallpaper() {
        return originalContext.clearWallpaper()
    }

    override fun startActivity(p0: Intent?) {
        return originalContext.startActivity(p0)
    }

    override fun startActivity(p0: Intent?, p1: Bundle?) {
        return originalContext.startActivity(p0, p1)
    }

    override fun startActivities(p0: Array<out Intent>?) {
        return originalContext.startActivities(p0)
    }

    override fun startActivities(p0: Array<out Intent>?, p1: Bundle?) {
        return originalContext.startActivities(p0, p1)
    }

    override fun startIntentSender(p0: IntentSender?, p1: Intent?, p2: Int, p3: Int, p4: Int) {
        return originalContext.startIntentSender(p0, p1, p2, p3, p4)
    }

    override fun startIntentSender(
        p0: IntentSender?,
        p1: Intent?,
        p2: Int,
        p3: Int,
        p4: Int,
        p5: Bundle?
    ) {
        return originalContext.startIntentSender(p0, p1, p2, p3, p4, p5)
    }

    override fun sendBroadcast(p0: Intent?) {
        return originalContext.sendBroadcast(p0)
    }

    override fun sendBroadcast(p0: Intent?, p1: String?) {
        return originalContext.sendBroadcast(p0, p1)
    }

    override fun sendOrderedBroadcast(p0: Intent?, p1: String?) {
        return originalContext.sendBroadcast(p0, p1)
    }

    override fun sendOrderedBroadcast(
        p0: Intent,
        p1: String?,
        p2: BroadcastReceiver?,
        p3: Handler?,
        p4: Int,
        p5: String?,
        p6: Bundle?
    ) {
        return originalContext.sendOrderedBroadcast(p0, p1, p2, p3, p4, p5, p6)
    }

    @RequiresPermission("android.permission.INTERACT_ACROSS_USERS")
    override fun sendBroadcastAsUser(p0: Intent?, p1: UserHandle?) {
        return originalContext.sendBroadcastAsUser(p0, p1)
    }

    @RequiresPermission("android.permission.INTERACT_ACROSS_USERS")
    override fun sendBroadcastAsUser(p0: Intent?, p1: UserHandle?, p2: String?) {
        return originalContext.sendBroadcastAsUser(p0, p1, p2)
    }

    @RequiresPermission("android.permission.INTERACT_ACROSS_USERS")
    override fun sendOrderedBroadcastAsUser(
        p0: Intent?,
        p1: UserHandle?,
        p2: String?,
        p3: BroadcastReceiver?,
        p4: Handler?,
        p5: Int,
        p6: String?,
        p7: Bundle?
    ) {
        return originalContext.sendOrderedBroadcastAsUser(p0, p1, p2, p3, p4, p5, p6, p7)
    }

    @Deprecated("Deprecated in Java")
    @RequiresPermission("android.permission.BROADCAST_STICKY")
    override fun sendStickyBroadcast(p0: Intent?) {
        return originalContext.sendStickyBroadcast(p0)
    }

    @Deprecated("Deprecated in Java")
    @RequiresPermission("android.permission.BROADCAST_STICKY")
    override fun sendStickyOrderedBroadcast(
        p0: Intent?,
        p1: BroadcastReceiver?,
        p2: Handler?,
        p3: Int,
        p4: String?,
        p5: Bundle?
    ) {
        return originalContext.sendStickyOrderedBroadcast(p0, p1, p2, p3, p4, p5)
    }

    @Deprecated("Deprecated in Java")
    @RequiresPermission("android.permission.BROADCAST_STICKY")
    override fun removeStickyBroadcast(p0: Intent?) {
        return originalContext.removeStickyBroadcast(p0)
    }

    @Deprecated("Deprecated in Java")
    @RequiresPermission(allOf = ["android.permission.BROADCAST_STICKY", "android.permission.INTERACT_ACROSS_USERS"])
    override fun sendStickyBroadcastAsUser(p0: Intent?, p1: UserHandle?) {
        return originalContext.sendStickyBroadcastAsUser(p0, p1)
    }

    @Deprecated("Deprecated in Java")
    @RequiresPermission(allOf = ["android.permission.BROADCAST_STICKY", "android.permission.INTERACT_ACROSS_USERS"])
    override fun sendStickyOrderedBroadcastAsUser(
        p0: Intent?,
        p1: UserHandle?,
        p2: BroadcastReceiver?,
        p3: Handler?,
        p4: Int,
        p5: String?,
        p6: Bundle?
    ) {
        return originalContext.sendStickyOrderedBroadcastAsUser(p0, p1, p2, p3, p4, p5, p6)
    }

    @Deprecated("Deprecated in Java")
    @RequiresPermission(allOf = ["android.permission.BROADCAST_STICKY", "android.permission.INTERACT_ACROSS_USERS"])
    override fun removeStickyBroadcastAsUser(p0: Intent?, p1: UserHandle?) {
        return originalContext.removeStickyBroadcastAsUser(p0, p1)
    }

    override fun registerReceiver(p0: BroadcastReceiver?, p1: IntentFilter?): Intent? {
        return originalContext.registerReceiver(p0, p1)
    }

    override fun registerReceiver(p0: BroadcastReceiver?, p1: IntentFilter?, p2: Int): Intent? {
        return originalContext.registerReceiver(p0, p1, p2)
    }

    override fun registerReceiver(
        p0: BroadcastReceiver?,
        p1: IntentFilter?,
        p2: String?,
        p3: Handler?
    ): Intent? {
        return originalContext.registerReceiver(p0, p1, p2, p3)
    }

    override fun registerReceiver(
        p0: BroadcastReceiver?,
        p1: IntentFilter?,
        p2: String?,
        p3: Handler?,
        p4: Int
    ): Intent? {
        return originalContext.registerReceiver(p0, p1, p2, p3, p4)
    }

    override fun unregisterReceiver(p0: BroadcastReceiver?) {
        return originalContext.unregisterReceiver(p0)
    }

    override fun startService(p0: Intent?): ComponentName? {
        return originalContext.startService(p0)
    }

    override fun startForegroundService(p0: Intent?): ComponentName? {
        return originalContext.startForegroundService(p0)
    }

    override fun stopService(p0: Intent?): Boolean {
        return originalContext.stopService(p0)
    }

    override fun bindService(p0: Intent, p1: ServiceConnection, p2: Int): Boolean {
        return originalContext.bindService(p0, p1, p2)
    }

    override fun unbindService(p0: ServiceConnection) {
        originalContext.unbindService(p0)
    }

    override fun startInstrumentation(p0: ComponentName, p1: String?, p2: Bundle?): Boolean {
        return originalContext.startInstrumentation(p0, p1, p2)
    }

    override fun getSystemService(p0: String): Any {
        return originalContext.getSystemService(p0)
    }

    override fun getSystemServiceName(p0: Class<*>): String? {
        return originalContext.getSystemServiceName(p0)
    }

    override fun checkPermission(p0: String, p1: Int, p2: Int): Int {
        return originalContext.checkPermission(p0, p1, p2)
    }

    override fun checkCallingPermission(p0: String): Int {
        return originalContext.checkCallingPermission(p0)
    }

    override fun checkCallingOrSelfPermission(p0: String): Int {
        return originalContext.checkCallingOrSelfPermission(p0)
    }

    override fun checkSelfPermission(p0: String): Int {
        return originalContext.checkSelfPermission(p0)
    }

    override fun enforcePermission(p0: String, p1: Int, p2: Int, p3: String?) {
        return originalContext.enforcePermission(p0, p1, p2, p3)
    }

    override fun enforceCallingPermission(p0: String, p1: String?) {
        return originalContext.enforceCallingPermission(p0, p1)
    }

    override fun enforceCallingOrSelfPermission(p0: String, p1: String?) {
        return originalContext.enforceCallingOrSelfPermission(p0, p1)
    }

    override fun grantUriPermission(p0: String?, p1: Uri?, p2: Int) {
        return originalContext.grantUriPermission(p0, p1, p2)
    }

    override fun revokeUriPermission(p0: Uri?, p1: Int) {
        return originalContext.revokeUriPermission(p0, p1)
    }

    override fun revokeUriPermission(p0: String?, p1: Uri?, p2: Int) {
        return originalContext.revokeUriPermission(p0, p1, p2)
    }

    override fun checkUriPermission(p0: Uri?, p1: Int, p2: Int, p3: Int): Int {
        return originalContext.checkUriPermission(p0, p1, p2, p3)
    }

    override fun checkUriPermission(
        p0: Uri?,
        p1: String?,
        p2: String?,
        p3: Int,
        p4: Int,
        p5: Int
    ): Int {
        return originalContext.checkUriPermission(p0, p1, p2, p3, p4, p5)
    }

    override fun checkCallingUriPermission(p0: Uri?, p1: Int): Int {
        return originalContext.checkCallingUriPermission(p0, p1)
    }

    override fun checkCallingOrSelfUriPermission(p0: Uri?, p1: Int): Int {
        return originalContext.checkCallingOrSelfUriPermission(p0, p1)
    }

    override fun enforceUriPermission(p0: Uri?, p1: Int, p2: Int, p3: Int, p4: String?) {
        return originalContext.enforceUriPermission(p0, p1, p2, p3, p4)
    }

    override fun enforceUriPermission(
        p0: Uri?,
        p1: String?,
        p2: String?,
        p3: Int,
        p4: Int,
        p5: Int,
        p6: String?
    ) {
        return originalContext.enforceUriPermission(p0, p1, p2, p3, p4, p5, p6)
    }

    override fun enforceCallingUriPermission(p0: Uri?, p1: Int, p2: String?) {
        return originalContext.enforceCallingUriPermission(p0, p1, p2)
    }

    override fun enforceCallingOrSelfUriPermission(p0: Uri?, p1: Int, p2: String?) {
        return originalContext.enforceCallingOrSelfUriPermission(p0, p1, p2)
    }

    override fun createPackageContext(p0: String?, p1: Int): Context {
        return originalContext.createPackageContext(p0, p1)
    }

    override fun createContextForSplit(p0: String?): Context {
        return originalContext.createContextForSplit(p0)
    }

    override fun createConfigurationContext(p0: Configuration): Context {
        return originalContext.createConfigurationContext(p0)
    }

    override fun createDisplayContext(p0: Display): Context {
        return originalContext.createDisplayContext(p0)
    }

    override fun createDeviceProtectedStorageContext(): Context {
        return originalContext.createDeviceProtectedStorageContext()
    }

    override fun isDeviceProtectedStorage(): Boolean {
        return originalContext.isDeviceProtectedStorage
    }


}