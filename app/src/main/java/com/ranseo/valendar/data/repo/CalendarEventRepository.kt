package com.ranseo.valendar.data.repo

import android.app.Application
import android.content.ContentValues
import android.net.Uri
import android.provider.CalendarContract
import com.ranseo.valendar.data.model.business.CalendarEventModel
import com.ranseo.valendar.util.Log
import com.ranseo.valendar.util.LogTag
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CalendarEventRepository @Inject constructor(
    application: Application
) {
    private val contentResolver = application.contentResolver

    suspend fun insertCalendarEvent(calendarEventModel: CalendarEventModel) = withContext(Dispatchers.IO){
        try {
            val values = ContentValues().apply {
                put(CalendarContract.Events.DTSTART, calendarEventModel.dTstart)
                put(CalendarContract.Events.DTEND, calendarEventModel.dtEnd)
                put(CalendarContract.Events.TITLE, calendarEventModel.title)
                put(CalendarContract.Events.DESCRIPTION, calendarEventModel.description)
                put(CalendarContract.Events.CALENDAR_ID, calendarEventModel.calId)
                put(CalendarContract.Events.EVENT_TIMEZONE, calendarEventModel.timeZone)
            }
            val uri : Uri = contentResolver.insert(CalendarContract.Events.CONTENT_URI, values)!!
            val eventId = uri.lastPathSegment!!.toLong()
            Log.log(TAG, "insertCalendarEvent() : Success, eventId = ${uri.lastPathSegment!!.toLong()}", LogTag.I)
            com.ranseo.valendar.data.model.Result.Success(eventId)
        }catch (error:Exception) {
            Log.log(TAG, "insertCalendarEvent() : Failure ${error.message}", LogTag.I)
            com.ranseo.valendar.data.model.Result.Error(error)
        }
    }

    companion object {
        private const val TAG = "CalendarEventRepository"
    }
}