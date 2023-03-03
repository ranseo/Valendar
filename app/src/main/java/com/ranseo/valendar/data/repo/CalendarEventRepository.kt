package com.ranseo.valendar.data.repo

import android.app.Application
import com.ranseo.valendar.data.datasource.CalendarEventCPDataSource
import com.ranseo.valendar.data.datasource.CalendarEventLocalDataSource
import com.ranseo.valendar.data.model.business.CalendarEventCPModel
import com.ranseo.valendar.data.model.business.CalendarEventLocalModel
import kotlinx.coroutines.withContext
import javax.inject.Inject
import com.ranseo.valendar.data.model.Result

class CalendarEventRepository @Inject constructor(private val calendarEventCPDataSource: CalendarEventCPDataSource, private val calendarEventLocalDataSource: CalendarEventLocalDataSource) {


    suspend fun insertCalendarEventCPModel(calendarEvent: CalendarEventCPModel) : Result<CalendarEventLocalModel> = calendarEventCPDataSource.insert(calendarEvent)


    suspend fun insertCalendarEventLocalModel(calendarEvent: CalendarEventLocalModel) {
        calendarEventLocalDataSource.insert(calendarEvent)
    }



    companion object {
        private const val TAG = "CalendarEventRepository"
    }
}