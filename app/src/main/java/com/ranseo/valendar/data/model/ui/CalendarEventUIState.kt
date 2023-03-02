package com.ranseo.valendar.data.model.ui

import androidx.recyclerview.widget.DiffUtil
import com.ranseo.valendar.CALENDAR_EVENT_WEATHER_TITLE
import com.ranseo.valendar.TIMEZONE_WEATHER
import com.ranseo.valendar.data.model.ui.WeatherUIState
import java.util.*

data class CalendarEventUIState(
    val calId: Long,
    val eventId: Long,
    val writingTime:String,
    val dTStart: Long,
    val dtEnd : Long,
    val title: String,
    val description: String,
    val timeZone: String
) {


    companion object{
        fun itemCallback() = object :DiffUtil.ItemCallback<CalendarEventUIState>() {
            override fun areItemsTheSame(
                oldItem: CalendarEventUIState,
                newItem: CalendarEventUIState
            ): Boolean = oldItem.eventId == newItem.eventId

            override fun areContentsTheSame(
                oldItem: CalendarEventUIState,
                newItem: CalendarEventUIState
            ): Boolean = oldItem == newItem

        }
    }

}
