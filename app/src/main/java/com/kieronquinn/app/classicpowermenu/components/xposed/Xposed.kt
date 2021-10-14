package com.kieronquinn.app.classicpowermenu.components.xposed

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.ApplicationInfo
import android.os.IBinder
import android.util.Log
import com.kieronquinn.app.classicpowermenu.BuildConfig
import com.kieronquinn.app.classicpowermenu.IGlobalActions
import com.kieronquinn.app.classicpowermenu.service.globalactions.GlobalActionsService
import com.kieronquinn.app.classicpowermenu.utils.extensions.SystemProperties_getString
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

    private var isHooked = false

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if(lpparam.packageName == "com.android.systemui") {
            when(SystemProperties_getString("ro.miui.ui.version.name", "null")) {
                "V125" -> hookMiuiSystemUI(lpparam)
            }
            hookSystemUI(lpparam)
        }
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
                tryBindService(param.args[0] as Context)
            }
        })

        //Calls the service if connected, and prevents the normal dialog showing if that returns true
        XposedHelpers.findAndHookMethod(globalActionsDialogClass, "handleShow", object: XC_MethodHook(){
            override fun beforeHookedMethod(param: MethodHookParam) {
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
                service?.hideGlobalActions()
            }
        })
    }

    private fun hookMiuiSystemUI(lpparam: XC_LoadPackage.LoadPackageParam) {
        val pluginManagerImplClass = XposedHelpers.findClass("com.android.systemui.shared.plugins.PluginManagerImpl", lpparam.classLoader)
        val m = pluginManagerImplClass.getDeclaredMethod("getClassLoader", ApplicationInfo::class.java)
        XposedBridge.hookMethod(m, object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                kotlin.runCatching {
                    val applicationInfo = param.args[0] as ApplicationInfo
                    val classLoader = param.result as? ClassLoader ?: return
                    if (applicationInfo.packageName != "miui.systemui.plugin" || isHooked) return

                    val globalActionsDialogClass = XposedHelpers.findClass("com.android.systemui.miui.globalactions.MiuiGlobalActionsDialog", classLoader)
                    //Bind the service when the dialog starts for the best chance of it being ready
                    XposedBridge.hookMethod(globalActionsDialogClass.constructors[0], object: XC_MethodHook(){
                        override fun afterHookedMethod(param: MethodHookParam) {
                            tryBindService(param.args[0] as Context)
                        }
                    })

                    //Calls the service if connected, and prevents the normal dialog showing if that returns true
                    XposedHelpers.findAndHookMethod(globalActionsDialogClass, "showDialog", object: XC_MethodHook(){
                        override fun beforeHookedMethod(param: MethodHookParam) {
                            service?.let {
                                if(showGlobalActions(it)){
                                    //We need to call onGlobalActionsShown to prevent the legacy dialog showing
                                    sendOnGlobalActionsShown(param)
                                    param.result = null
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
                    XposedHelpers.findAndHookMethod(globalActionsDialogClass, "dismiss", Int::class.java, object: XC_MethodHook(){
                        override fun beforeHookedMethod(param: MethodHookParam?) {
                            service?.hideGlobalActions()
                        }
                    })
                    isHooked = true
                }.onFailure {
                    Log.d(TAG, "Failed to hookMiuiSystemUI", it)
                    XposedBridge.log(it)
                }
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