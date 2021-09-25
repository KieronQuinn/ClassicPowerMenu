package com.android.systemui.backup

class BackupHelper {

    //Bare minimum to maintain basic functionality

    companion object {
        val controlsDataLock = Any()
        const val ACTION_RESTORE_FINISHED = "com.android.systemui.backup.RESTORE_FINISHED"
    }

}