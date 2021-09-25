package com.kieronquinn.app.classicpowermenu.model.service

import android.app.IServiceConnection
import android.os.Parcel
import android.os.Parcelable

/**
 *  Holds data related to binding a service with IClassicPowerMenu.unbindServicePrivileged, and is
 *  used to transfer the local service connection to the root service.
 *
 *  Due to restrictions with how AIDL building works, IClassicPowerMenu cannot see the hidden
 *  [IServiceConnection] during building, and so we pass them in a [Parcelable]
 *  instead.
 */
data class ServiceUnbindContainer(val serviceConnection: IServiceConnection): Parcelable {
    constructor(parcel: Parcel) : this(IServiceConnection.Stub.asInterface(parcel.readStrongBinder()))

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeStrongBinder(serviceConnection.asBinder())
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ServiceUnbindContainer> {
        override fun createFromParcel(parcel: Parcel): ServiceUnbindContainer {
            return ServiceUnbindContainer(parcel)
        }

        override fun newArray(size: Int): Array<ServiceUnbindContainer?> {
            return arrayOfNulls(size)
        }
    }
}
