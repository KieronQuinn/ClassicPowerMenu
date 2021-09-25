package com.kieronquinn.app.classicpowermenu.model.service

import android.app.IApplicationThread
import android.content.Intent
import android.os.Parcel
import android.os.Parcelable
import com.kieronquinn.app.classicpowermenu.utils.extensions.readNullableString
import com.kieronquinn.app.classicpowermenu.utils.extensions.writeNullableString

data class BroadcastContainer(val thread: IApplicationThread): Parcelable {

    constructor(parcel: Parcel) : this(
        IApplicationThread.Stub.asInterface(parcel.readStrongBinder())
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeStrongBinder(thread.asBinder())
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BroadcastContainer> {
        override fun createFromParcel(parcel: Parcel): BroadcastContainer {
            return BroadcastContainer(parcel)
        }

        override fun newArray(size: Int): Array<BroadcastContainer?> {
            return arrayOfNulls(size)
        }
    }
}
