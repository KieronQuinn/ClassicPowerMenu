package com.kieronquinn.app.classicpowermenu.service.root

import android.content.Intent
import android.os.IBinder
import com.kieronquinn.app.classicpowermenu.service.root.impl.CPMRootServiceImpl
import com.topjohnwu.superuser.ipc.RootService

class CPMRootService: RootService() {

    override fun onBind(intent: Intent): IBinder {
        return CPMRootServiceImpl()
    }

    override fun onUnbind(intent: Intent): Boolean {
        return false
    }

}