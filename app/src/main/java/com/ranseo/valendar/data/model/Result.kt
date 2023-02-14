package com.ranseo.valendar.data.model

sealed class Result<out T: Any> {
    data class Success<out T: Any>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()

    override fun toString(): String {
        return when(this){
            is Success<*> -> "Successs[data=$data]"
            is Error -> "Error[exception=$exception]"
        }
    }
}