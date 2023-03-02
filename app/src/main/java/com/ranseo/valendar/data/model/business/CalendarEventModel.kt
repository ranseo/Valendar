package com.ranseo.valendar.data.model.business

import com.ranseo.valendar.CALENDAR_EVENT_WEATHER_TITLE
import com.ranseo.valendar.TIMEZONE_WEATHER
import com.ranseo.valendar.data.model.ui.WeatherUIState
import java.util.*

data class CalendarEventModel(
    val calId: Long,
    val dTStart: Long,
    val dTEnd : Long,
    val title: String,
    val description: String,
    val timeZone: String
) {


    companion object{
        @JvmStatic
        fun getCalendarEventFromWeather(weather:WeatherUIState) : CalendarEventModel {

            val calId = 3L
            val year = weather.baseDate.toString().substring(0..3).toInt()
            val month = weather.baseDate.toString().substring(4..5).toInt() - 1
            val day = weather.baseDate.toString().substring(6..7).toInt()
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
            val description = "${weather.sky}\n${weather.tmp}\n${weather.pty}\n${weather.pop}"
            val timeZone = TIMEZONE_WEATHER

            return CalendarEventModel(calId, dTStart, dTEnd, title, description, timeZone)
        }
    }

}