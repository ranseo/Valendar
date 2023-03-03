package com.ranseo.valendar.data.datasource

import com.ranseo.valendar.data.model.business.CalendarEventLocalModel
import com.ranseo.valendar.room.ValendarDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CalendarEventLocalDataSource @Inject constructor(private val valendarDao: ValendarDao){

    suspend fun insert(calendarEventLocalModel: CalendarEventLocalModel) = withContext(Dispatchers.IO){
        valendarDao.insertCalendarEvent(calendarEventLocalModel)
    }

    suspend fun getCalendarEvents(start:Long, end:Long) : Flow<List<CalendarEventLocalModel>> {
        return valendarDao.getCalendarEvents(start,end)
    }

}