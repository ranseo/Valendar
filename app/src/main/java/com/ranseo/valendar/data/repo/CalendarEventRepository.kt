package com.ranseo.valendar.data.repo

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import com.ranseo.valendar.data.datasource.CalendarEventCPDataSource
import com.ranseo.valendar.data.datasource.CalendarEventLocalDataSource
import com.ranseo.valendar.data.model.business.CalendarEventCPModel
import com.ranseo.valendar.data.model.business.CalendarEventLocalModel
import kotlinx.coroutines.withContext
import javax.inject.Inject
import com.ranseo.valendar.data.model.Result
import com.ranseo.valendar.data.model.ui.CalendarEventUIState
import com.ranseo.valendar.data.model.ui.CalendarEventUIStateContainer
import com.ranseo.valendar.util.Log
import com.ranseo.valendar.util.LogTag
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CalendarEventRepository @Inject constructor(private val calendarEventCPDataSource: CalendarEventCPDataSource, private val calendarEventLocalDataSource: CalendarEventLocalDataSource) {

    fun queryCalendarEventLocalModel(start:Long, end:Long) : Flow<List<CalendarEventUIState>> =
        calendarEventLocalDataSource.query(start, end).map {
            CalendarEventUIStateContainer.getCalendarEvent(it).calendarEvent
        }


    private suspend fun insertCPModel(calendarEvent: CalendarEventCPModel) : Result<CalendarEventLocalModel> = calendarEventCPDataSource.insert(calendarEvent)

    private suspend fun insertLocalModel(calendarEvent: CalendarEventLocalModel) {
        calendarEventLocalDataSource.insert(calendarEvent)
    }

    suspend fun insertCPAndThenLocal(cp: CalendarEventCPModel) : Result<CalendarEventLocalModel> =  withContext(Dispatchers.IO){
        when(val res = insertCPModel(cp)) { // Result<CalendarEventLocalModel>
            is Result.Success<CalendarEventLocalModel> -> {
                val local = res.data
                insertLocalModel(local)
                Result.Success(local)
            }
            is Result.Error -> {
                Result.Error(res.exception)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    suspend fun updateCalendarEvent(calendarEvent: CalendarEventLocalModel) {
        when(val row = calendarEventCPDataSource.update(calendarEvent)) {
            is Result.Success<Int> -> {
                Log.log(TAG, "updateCalendarEvent() : ${row.data}", LogTag.I)
            }
            is Result.Error -> {
                Log.log(TAG, "updateCalendarEvent() : ${row.exception}", LogTag.I)
            }
        }

        calendarEventLocalDataSource.update(calendarEvent)
    }

    suspend fun deleteCalendarEvent(calendarEvent: CalendarEventLocalModel) : Result<Int> {
        calendarEventLocalDataSource.delete(calendarEvent)
        return calendarEventCPDataSource.delete(calendarEvent)
    }



    companion object {
        private const val TAG = "CalendarEventRepository"
    }
}