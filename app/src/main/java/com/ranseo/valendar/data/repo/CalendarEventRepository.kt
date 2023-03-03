package com.ranseo.valendar.data.repo

import android.app.Application
import com.ranseo.valendar.data.datasource.CalendarEventCPDataSource
import com.ranseo.valendar.data.datasource.CalendarEventLocalDataSource
import com.ranseo.valendar.data.model.business.CalendarEventCPModel
import com.ranseo.valendar.data.model.business.CalendarEventLocalModel
import kotlinx.coroutines.withContext
import javax.inject.Inject
import com.ranseo.valendar.data.model.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

class CalendarEventRepository @Inject constructor(private val calendarEventCPDataSource: CalendarEventCPDataSource, private val calendarEventLocalDataSource: CalendarEventLocalDataSource) {


    private suspend fun insertCPModel(calendarEvent: CalendarEventCPModel) : Result<CalendarEventLocalModel> = calendarEventCPDataSource.insert(calendarEvent)

    private suspend fun insertLocalModel(calendarEvent: CalendarEventLocalModel) {
        calendarEventLocalDataSource.insert(calendarEvent)
    }

    fun queryCalendarEventLocalModel(start:Long, end:Long) : Flow<List<CalendarEventLocalModel>> = calendarEventLocalDataSource.query(start, end)


    suspend fun insertCPModelAndThenLocalModel(cp: CalendarEventCPModel) = withContext(Dispatchers.IO){
        when(val res = insertCPModel(cp)) { // Result<CalendarEventLocalModel>
            is Result.Success<CalendarEventLocalModel> -> {
                val local = res.data
                insertLocalModel(local)
            }
            is Result.Error -> {

            }
        }
    }



    companion object {
        private const val TAG = "CalendarEventRepository"
    }
}