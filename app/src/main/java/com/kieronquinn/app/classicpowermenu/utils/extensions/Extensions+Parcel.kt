package com.kieronquinn.app.classicpowermenu.utils.extensions

import android.os.IBinder
import android.os.Parcel

fun Parcel.writeNullableString(value: String?){
    if(value != null){
        writeBoolean(false)
        writeString(value)
    }else{
        writeBoolean(true)
    }
}

fun Parcel.readNullableString(): String? {
    val isNull = readBoolean()
    return if(!isNull){
        readString()
    }else null
}

fun Parcel.readStrongBinderOptional(): IBinder? {
    val isNull = readBoolean()
    return if(isNull) null
    else readStrongBinder()
}

fun Parcel.writeStrongBinderOptional(binder: IBinder?){
    writeBoolean(binder == null)
    if(binder != null){
        writeStrongBinder(binder)
    }
}