package com.ranseo.valendar.data.model.business

import com.ranseo.valendar.CALENDAR_EVENT_WEATHER_TITLE
import com.ranseo.valendar.TIMEZONE_WEATHER
import com.ranseo.valendar.data.model.ui.WeatherUIState
import java.util.*

data class CalendarEventCPModel(
    val calId: Long,
    val dTStart: Long,
    val dTEnd : Long,
    val title: String,
    val description: String,
    val timeZone: String,
    val baseTime: Int
) {

    companion object{
        @JvmStatic
        fun getCalendarEventFromWeather(weather:WeatherUIState) : CalendarEventCPModel {

            val calId = 3L
            val year = weather.baseDate.toString().substring(0..3).toInt()
            val month = weather.baseDate.toString().substring(4..5).toInt() - 1
            val day = weather.baseDate.toString().substring(6..7).toInt()
            val baseTime = weather.baseTime
            val (hour, min)= run {
                    val time = ("%04d".format(weather.baseTime))
                    val hour = time.substring(0, 2).toInt()
                    val min = time.substring(2, 4).toInt()
                    (hour to min)
                }

            val dTStart = Calendar.getInstance().run {
                set(year, month, day,hour, min)
                timeInMillis
            }

            val dTEnd = Calendar.getInstance().run {
                set(year, month, day,hour, min)
                timeInMillis
            }
            val title = CALENDAR_EVENT_WEATHER_TITLE
            val description = "${weather.sky},${weather.tmp}\n${weather.pty},${weather.pop}"
            val timeZone = TIMEZONE_WEATHER

            return CalendarEventCPModel(calId, dTStart, dTEnd, title, description, timeZone, baseTime)
        }
    }
}

fun CalendarEventCPModel.asLocalModel(eventId:Long) : CalendarEventLocalModel {
    return CalendarEventLocalModel(
        eventId = eventId,
        dTStart= this.dTStart,
        dTEnd = this.dTEnd,
        title = this.title,
        description = this.description,
        timeZone = this.timeZone,
        baseTime = this.baseTime,
        calId = this.calId
    )
}
