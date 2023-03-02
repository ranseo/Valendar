package com.ranseo.valendar.room

import androidx.lifecycle.LiveData
import androidx.room.*
import com.ranseo.valendar.data.model.business.CalendarEventLocalModel
import com.ranseo.valendar.data.model.business.WeatherLocalModel
import kotlinx.coroutines.flow.Flow

@Dao
interface ValendarDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(weather: WeatherLocalModel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(weathers:List<WeatherLocalModel>)

    @Update
    suspend fun update(weather: WeatherLocalModel)

    @Query("SELECT * FROM weather_table WHERE :baseDate = base_date")
    suspend fun getWeather(baseDate: Int) : List<WeatherLocalModel>


    //CalendarEventLocalModel::class
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCalendarEvent(calendarEvent:CalendarEventLocalModel)

    @Update
    suspend fun updateCalendarEvent(calendarEvent: CalendarEventLocalModel)

    @Query("SELECT * FROM calendar_event_table WHERE (d_t_start BETWEEN :start AND :end) AND (d_t_end BETWEEN :start AND :end)")
    fun getCalendarEvents(start:Long, end:Long) : Flow<List<CalendarEventLocalModel>>

}