package com.ranseo.valendar.model

data class CalendarEvent(
    val calendarId: Long,
    val accountName:String,
    val accountType:String,
    val organizer : String,
    val title:String,
    val eventLocation:String,
    val description : String,
    val dtStart:Long,
    val dtEnd:Long,

) {
}