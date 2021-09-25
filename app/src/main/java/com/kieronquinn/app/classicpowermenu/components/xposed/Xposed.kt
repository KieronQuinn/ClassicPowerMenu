package com.kieronquinn.app.classicpowermenu.components.xposed

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import com.kieronquinn.app.classicpowermenu.BuildConfig
import com.kieronquinn.app.classicpowermenu.IGlobalActions
import com.kieronquinn.app.classicpowermenu.service.globalactions.GlobalActionsService
import de.robv.android.xposed.*
import de.robv.android.xposed.callbacks.XC_LoadPackage

class Xposed: IXposedHookLoadPackage, ServiceConnection {

    companion object {
        private const val TAG = "CPMXposed"
    }

    private val serviceIntent by lazy {
        Intent().apply {
            `package` = BuildConfig.APPLICATION_ID
            component = ComponentName(BuildConfig.APPLICATION_ID, GlobalActionsService::class.java.name)
        }
    }

    private var service: IGlobalActions? = null

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if(lpparam.packageName == "com.android.systemui") hookSystemUI(lpparam)
        if(lpparam.packageName == BuildConfig.APPLICATION_ID) hookSelf(lpparam)
    }

    /**
     *  Self-hooks [XposedSelfHook.isXposedHooked] to return true
     */
    private fun hookSelf(lpparam: XC_LoadPackage.LoadPackageParam){
        XposedHelpers.findAndHookMethod(XposedSelfHook::class.java.name, lpparam.classLoader, "isXposedHooked", object: XC_MethodReplacement() {
            override fun replaceHookedMethod(param: MethodHookParam): Any {
                param.result = true
                return true
            }
        })
    }

    private fun hookSystemUI(lpparam: XC_LoadPackage.LoadPackageParam) {
        val globalActionsDialogClass = XposedHelpers.findClass("com.android.systemui.globalactions.GlobalActionsDialog", lpparam.classLoader)

        //Bind the service when the dialog starts for the best chance of it being ready
        XposedBridge.hookMethod(globalActionsDialogClass.constructors[0], object: XC_MethodHook(){
            override fun afterHookedMethod(param: MethodHookParam) {
                super.afterHookedMethod(param)
                tryBindService(param.args[0] as Context)
            }
        })

        //Calls the service if connected, and prevents the normal dialog showing if that returns true
        XposedHelpers.findAndHookMethod(globalActionsDialogClass, "handleShow", object: XC_MethodHook(){
            override fun beforeHookedMethod(param: MethodHookParam) {
                super.beforeHookedMethod(param)
                service?.let {
                    if(showGlobalActions(it)){
                        //We need to call onGlobalActionsShown to prevent the legacy dialog showing
                        sendOnGlobalActionsShown(param)
                        param.result = true
                        return
                    }
                } ?: run {
                    //Bind service for next time
                    val context = XposedHelpers.getObjectField(param.thisObject, "mContext") as Context
                    tryBindService(context)
                }
            }
        })

        //Optionally calls to the service, if it exists
        XposedHelpers.findAndHookMethod(globalActionsDialogClass, "dismissDialog", object: XC_MethodHook(){
            override fun beforeHookedMethod(param: MethodHookParam?) {
                super.beforeHookedMethod(param)
                service?.hideGlobalActions()
            }
        })
    }

    private fun showGlobalActions(service: IGlobalActions): Boolean {
        return try {
            service.showGlobalActions()
        }catch (e: Exception){
            false
        }
    }

    private fun sendOnGlobalActionsShown(param: XC_MethodHook.MethodHookParam){
        val mWindowManagerFuncs = XposedHelpers.getObjectField(param.thisObject, "mWindowManagerFuncs")
        XposedHelpers.callMethod(mWindowManagerFuncs, "onGlobalActionsShown")
    }

    private fun tryBindService(context: Context) {
        try {
            context.bindService(serviceIntent, this, Context.BIND_AUTO_CREATE)
        }catch (e: Exception){
            Log.d(TAG, "Failed to bind to GlobalActionsDialog service", e)
            XposedBridge.log(e)
        }
    }

    override fun onServiceConnected(component: ComponentName, binder: IBinder) {
        service = IGlobalActions.Stub.asInterface(binder)
    }

    override fun onServiceDisconnected(component: ComponentName) {
        service = null
    }

}