package com.ranseo.valendar.model

data class CalendarInfo(
    val calendarId:Long,
    val accountName:String,
    val accountType:String,
    val ownerName:String,
    val displayName:String
) {
}