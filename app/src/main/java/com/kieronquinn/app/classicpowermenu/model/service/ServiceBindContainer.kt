package com.kieronquinn.app.classicpowermenu.model.service

import android.app.IApplicationThread
import android.app.IServiceConnection
import android.content.Intent
import android.os.IBinder
import android.os.Parcel
import android.os.Parcelable
import com.kieronquinn.app.classicpowermenu.utils.extensions.readStrongBinderOptional
import com.kieronquinn.app.classicpowermenu.utils.extensions.writeStrongBinderOptional

/**
 *  Holds data related to binding a service with IClassicPowerMenu.bindServicePrivileged, and is
 *  used to transfer the local (fully usable) application thread & application token to the root
 *  service, where one is not available.
 *
 *  Due to restrictions with how AIDL building works, IClassicPowerMenu cannot see the hidden
 *  [IApplicationThread] and [IServiceConnection] during building, and so we pass them in a [Parcelable]
 *  instead.
 */
data class ServiceBindContainer(
    val thread: IApplicationThread?,
    val activityToken: IBinder?,
    val serviceConnection: IServiceConnection
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readStrongBinderOptional()?.let { IApplicationThread.Stub.asInterface(it) },
        parcel.readStrongBinderOptional(),
        IServiceConnection.Stub.asInterface(parcel.readStrongBinder())
    )

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeStrongBinderOptional(thread?.asBinder())
        parcel.writeStrongBinderOptional(activityToken)
        parcel.writeStrongBinder(serviceConnection.asBinder())
    }

    companion object CREATOR : Parcelable.Creator<ServiceBindContainer> {
        override fun createFromParcel(parcel: Parcel): ServiceBindContainer {
            return ServiceBindContainer(parcel)
        }

        override fun newArray(size: Int): Array<ServiceBindContainer?> {
            return arrayOfNulls(size)
        }

    }

}
