package com.kieronquinn.app.classicpowermenu.service.container

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import com.kieronquinn.app.classicpowermenu.IClassicPowerMenu
import com.kieronquinn.app.classicpowermenu.service.root.CPMRootService
import com.topjohnwu.superuser.ipc.RootService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

interface CPMServiceContainer {

    suspend fun <T> runWithService(block: suspend (IClassicPowerMenu) -> T): T
    fun <T> runWithServiceIfAvailable(block: (IClassicPowerMenu) -> T?): T?
    suspend fun unbindServiceIfNeeded()

}

class CPMServiceContainerImpl(context: Context): CPMServiceContainer {

    private var serviceInstance: IClassicPowerMenu? = null
    private var serviceConnection: ServiceConnection? = null

    private val serviceIntent = Intent(context, CPMRootService::class.java)

    override suspend fun <T> runWithService(block: suspend (IClassicPowerMenu) -> T): T {
        val service = withContext(Dispatchers.Main){
            getServiceLocked()
        }
        return block(service)
    }

    private suspend fun getServiceLocked() = suspendCoroutine<IClassicPowerMenu> { resume ->
        serviceInstance?.let {
            resume.resume(it)
            return@suspendCoroutine
        }
        val serviceConnection = object: ServiceConnection {
            override fun onServiceConnected(component: ComponentName, binder: IBinder) {
                serviceInstance = IClassicPowerMenu.Stub.asInterface(binder)
                serviceConnection = this
                resume.resume(serviceInstance!!)
            }

            override fun onServiceDisconnected(component: ComponentName) {
                serviceInstance = null
                serviceConnection = null
            }
        }
        RootService.bind(serviceIntent, serviceConnection)
    }

    override suspend fun unbindServiceIfNeeded() {
        serviceConnection?.let {
            RootService.unbind(it)
        }
    }

    override fun <T> runWithServiceIfAvailable(block: (IClassicPowerMenu) -> T?): T? {
        return serviceInstance?.let {
            block(it)
        }
    }

}