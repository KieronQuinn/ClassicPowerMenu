package com.kieronquinn.app.classicpowermenu.model.service

import android.app.IApplicationThread
import android.content.Intent
import android.os.Parcel
import android.os.Parcelable
import com.kieronquinn.app.classicpowermenu.utils.extensions.readNullableString
import com.kieronquinn.app.classicpowermenu.utils.extensions.readStrongBinderOptional
import com.kieronquinn.app.classicpowermenu.utils.extensions.writeNullableString
import com.kieronquinn.app.classicpowermenu.utils.extensions.writeStrongBinderOptional

data class ActivityContainer(val thread: IApplicationThread?): Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readStrongBinderOptional()?.let { IApplicationThread.Stub.asInterface(it) }
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeStrongBinderOptional(thread?.asBinder())
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ActivityContainer> {
        override fun createFromParcel(parcel: Parcel): ActivityContainer {
            return ActivityContainer(parcel)
        }

        override fun newArray(size: Int): Array<ActivityContainer?> {
            return arrayOfNulls(size)
        }
    }
}
