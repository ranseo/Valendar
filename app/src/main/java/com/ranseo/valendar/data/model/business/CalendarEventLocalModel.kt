package com.ranseo.valendar.data.model.business

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ranseo.valendar.CALENDAR_EVENT_WEATHER_TITLE
import com.ranseo.valendar.TIMEZONE_WEATHER
import com.ranseo.valendar.data.model.ui.WeatherUIState
import java.util.*

@Entity(tableName = "calendar_event_table")
data class CalendarEventLocalModel(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name="event_id")
    val eventId: Long,
    @ColumnInfo(name="d_t_start")
    val dTStart: Long,
    @ColumnInfo(name="d_t_end")
    val dTEnd : Long,
    @ColumnInfo(name="title")
    val title: String,
    val description: String,
    @ColumnInfo(name="time_zone")
    val timeZone: String,
    @ColumnInfo(name = "base_time")
    val baseTime: Int,
    @ColumnInfo(name="cal_id")
    val calId: Long,
) {
}



