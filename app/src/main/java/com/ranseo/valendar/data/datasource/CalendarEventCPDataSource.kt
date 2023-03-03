package com.ranseo.valendar.data.datasource

import android.app.Application
import android.content.ContentValues
import android.net.Uri
import android.provider.CalendarContract
import com.ranseo.valendar.data.model.Result
import com.ranseo.valendar.data.model.business.CalendarEventCPModel
import com.ranseo.valendar.data.model.business.asLocalModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CalendarEventCPDataSource @Inject constructor(application: Application) {
    private val contentResolver = application.contentResolver

    suspend fun insert(calendarEventCPModel: CalendarEventCPModel) = withContext(
        Dispatchers.IO){
        try {
            val values = ContentValues().apply {
                put(CalendarContract.Events.DTSTART, calendarEventCPModel.dTStart)
                put(CalendarContract.Events.DTEND, calendarEventCPModel.dTEnd)
                put(CalendarContract.Events.TITLE, calendarEventCPModel.title)
                put(CalendarContract.Events.DESCRIPTION, calendarEventCPModel.description)
                put(CalendarContract.Events.CALENDAR_ID, calendarEventCPModel.calId)
                put(CalendarContract.Events.EVENT_TIMEZONE, calendarEventCPModel.timeZone)
            }
            val uri : Uri = contentResolver.insert(CalendarContract.Events.CONTENT_URI, values)!!
            val eventId = uri.lastPathSegment!!.toLong()
            val localModel = calendarEventCPModel.asLocalModel(eventId)
            Result.Success(localModel)
        }catch (error:Exception) {
            Result.Error(error)
        }
    }
}