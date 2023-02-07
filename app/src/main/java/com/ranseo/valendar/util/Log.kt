package com.ranseo.valendar.util

import android.util.Log

enum class LogTag {
    I,
    D,
    E,
    W
}
object Log {
    fun log(call:String ,msg:String, tag:LogTag){
        when(tag) {
            LogTag.I -> {Log.i(call, msg)}
            LogTag.D -> {Log.d(call, msg)}
            LogTag.E -> {Log.e(call, msg)}
            LogTag.W -> {Log.w(call, msg)}
        }
    }
}