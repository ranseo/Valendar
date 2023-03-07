package com.ranseo.valendar.data.model.ui

import android.os.Parcel
import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil
import com.ranseo.valendar.CALENDAR_EVENT_WEATHER_TITLE
import com.ranseo.valendar.TIMEZONE_WEATHER
import com.ranseo.valendar.data.model.business.CalendarEventLocalModel
import com.ranseo.valendar.data.model.business.WeatherLocalModel
import com.ranseo.valendar.data.model.business.WeatherLocalModelContainer
import com.ranseo.valendar.data.model.ui.WeatherUIState
import java.util.*

data class CalendarEventUIState(
    val calId: Long,
    val eventId: Long,
    val baseTime: String,
    val dTStart: Long,
    val dTEnd: Long,
    val title: String,
    val description: String,
    val timeZone: String
) : Parcelable {


    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readLong(),
        parcel.readString() ?: "",
        parcel.readLong(),
        parcel.readLong(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    ) {
    }




    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(calId)
        parcel.writeLong(eventId)
        parcel.writeString(baseTime)
        parcel.writeLong(dTStart)
        parcel.writeLong(dTEnd)
        parcel.writeString(title)
        parcel.writeString(description)
        parcel.writeString(timeZone)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CalendarEventUIState> {
        override fun createFromParcel(parcel: Parcel): CalendarEventUIState {
            return CalendarEventUIState(parcel)
        }

        override fun newArray(size: Int): Array<CalendarEventUIState?> {
            return arrayOfNulls(size)
        }

        fun itemCallback() = object : DiffUtil.ItemCallback<CalendarEventUIState>() {
            override fun areItemsTheSame(
                oldItem: CalendarEventUIState,
                newItem: CalendarEventUIState
            ): Boolean = oldItem.eventId == newItem.eventId

            override fun areContentsTheSame(
                oldItem: CalendarEventUIState,
                newItem: CalendarEventUIState
            ): Boolean = oldItem == newItem

        }

        fun getEmpty() : CalendarEventUIState {
            return CalendarEventUIState(0L,0L,"",0L,0L,"","","")
        }
    }
}

data class CalendarEventUIStateContainer(
    val calendarEvent: List<CalendarEventUIState>
) {
    companion object {
        fun getCalendarEvent(calendarEvents: List<CalendarEventLocalModel>): CalendarEventUIStateContainer {
            return CalendarEventUIStateContainer(calendarEvents.map {
                CalendarEventUIState(
                    calId = it.calId,
                    eventId = it.eventId,
                    baseTime = convertBaseTimeAsString( it.baseTime),
                    dTStart =  it.dTStart,
                    dTEnd = it.dTEnd,
                    title = it.title,
                    description = it.description,
                    timeZone = it.timeZone
                )
            })
        }

        fun convertBaseTimeAsString(baseTime: Int): String {
            return run {
                val time = ("%04d".format(baseTime))
                "${time.substring(0, 2)}시${time.substring(2, 4)}분"
            }
        }
    }
}

