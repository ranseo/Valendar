package com.ranseo.valendar.data.datasource

import android.app.Application
import android.content.ContentUris
import android.content.ContentValues
import android.net.Uri
import android.os.Build
import android.provider.CalendarContract
import androidx.annotation.RequiresApi
import com.ranseo.valendar.data.model.Result
import com.ranseo.valendar.data.model.business.CalendarEventCPModel
import com.ranseo.valendar.data.model.business.CalendarEventLocalModel
import com.ranseo.valendar.data.model.business.asLocalModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import com.ranseo.valendar.data.model.Result as MyResult

class CalendarEventCPDataSource @Inject constructor(application: Application) {
    private val contentResolver = application.contentResolver

    suspend fun insert(calendarEventCPModel: CalendarEventCPModel) = withContext(
        Dispatchers.IO
    ) {
        try {
            val values = ContentValues().apply {
                put(CalendarContract.Events.DTSTART, calendarEventCPModel.dTStart)
                put(CalendarContract.Events.DTEND, calendarEventCPModel.dTEnd)
                put(CalendarContract.Events.TITLE, calendarEventCPModel.title)
                put(CalendarContract.Events.DESCRIPTION, calendarEventCPModel.description)
                put(CalendarContract.Events.CALENDAR_ID, calendarEventCPModel.calId)
                put(CalendarContract.Events.EVENT_TIMEZONE, calendarEventCPModel.timeZone)
            }
            val uri: Uri = contentResolver.insert(CalendarContract.Events.CONTENT_URI, values)!!
            val eventId = uri.lastPathSegment!!.toLong()
            val localModel = calendarEventCPModel.asLocalModel(eventId)
            Result.Success(localModel)
        } catch (error: Exception) {
            Result.Error(error)
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    suspend fun update(calendarEvent: CalendarEventLocalModel) = withContext(Dispatchers.IO) {
        try {
            val values = ContentValues().apply {
                put(CalendarContract.Events.TITLE, calendarEvent.title)
                put(CalendarContract.Events.DTSTART, calendarEvent.dTStart)
                put(CalendarContract.Events.DTSTART, calendarEvent.dTEnd)
                put(CalendarContract.Events.DESCRIPTION, calendarEvent.description)
            }

            val updateUri: Uri = ContentUris.withAppendedId(
                CalendarContract.Events.CONTENT_URI,
                calendarEvent.eventId
            )
            val selection = "(${CalendarContract.Events.CALENDAR_ID} = ?)"
            val selectionArgs: Array<String> = arrayOf("${calendarEvent.calId}")

            val rows: Int = contentResolver.update(updateUri, values, null, null)

            MyResult.Success(rows)
        } catch (error: java.lang.Exception) {
            MyResult.Error(error)
        }
    }

    suspend fun delete(calendarEvent: CalendarEventLocalModel) : Result<Int> = withContext(Dispatchers.IO) {
        try {
            val uri: Uri = ContentUris.withAppendedId(
                CalendarContract.Events.CONTENT_URI,
                calendarEvent.eventId
            )
//            val selection = "((${CalendarContract.Events.CALENDAR_ID} = ?))"
//            val selectionArgs: Array<String> = arrayOf("${calendarEvent.calId}")
            val rows: Int = contentResolver.delete(uri, null, null)
            Result.Success(rows)
        } catch (error: Exception) {
            Result.Error(error)
        }
    }
}