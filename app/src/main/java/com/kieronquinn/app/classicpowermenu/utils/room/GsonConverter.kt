package com.kieronquinn.app.classicpowermenu.utils.room


import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object GsonSetConverter: GsonConverter<Set<String>>(typeToken())

abstract class GsonConverter<T>(type: TypeToken<T>): KoinComponent {

    companion object {
        @JvmStatic
        protected inline fun <reified T> typeToken(): TypeToken<T> {
            return object: TypeToken<T>(){}
        }
    }

    private val gson by inject<Gson>()
    private val typeToken = type.type

    @TypeConverter
    fun fromString(value: String): T {
        return gson.fromJson(value, typeToken)
    }

    @TypeConverter
    fun fromObject(obj: T): String {
        return gson.toJson(obj)
    }

}