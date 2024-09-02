package com.kieronquinn.app.classicpowermenu.utils


sealed class Result<T>(val time: Long = System.currentTimeMillis()) {

    data class Failed<T>(val code: Int = 999, val cached: T? = null): Result<T>()
    data class Success<T>(val data: T): Result<T>()

    fun unwrap(): T? {
        return if(this is Success) data else null
    }

    /**
     *  Converts type of wrapped data, this is only designed to work with [Failed], but if the
     *  data is castable from [T] to [O], then [Success] will also work. Alternatively, passing
     *  a [convert] block will allow type conversion.
     */
    @Suppress("UNCHECKED_CAST")
    fun <O> mutate(convert: ((T) -> O) = { it as O }): Result<O> {
        return when(this){
            is Success -> Success(convert(data))
            is Failed -> Failed(code)
        }
    }
}