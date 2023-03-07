package com.ranseo.valendar.ui

import android.view.View

interface OnClickListener<T> {
    fun <T> onClick(p0:T)
    fun onLongClick() : Boolean
}