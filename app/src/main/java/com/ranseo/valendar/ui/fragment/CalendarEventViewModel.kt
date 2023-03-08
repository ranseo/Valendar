package com.ranseo.valendar.ui.fragment

import android.app.Application
import android.text.Editable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ranseo.valendar.data.model.ui.CalendarEventUIState
import com.ranseo.valendar.data.model.ui.CalendarEventUIStateContainer
import com.ranseo.valendar.domain.DeleteCalendarEventUseCase
import com.ranseo.valendar.domain.UpdateCalendarEventUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class UpdateTag {
    TITLE,
    DTSTART,
    DTEND,
    DESCRIPTION,
    BASETIME
}

@HiltViewModel
class CalendarEventViewModel @Inject constructor(
    application: Application,
    val updateCalendarEventUseCase: UpdateCalendarEventUseCase,
    val deleteCalendarEventUseCase: DeleteCalendarEventUseCase
) : AndroidViewModel(application) {

    private val _calendarEvent = MutableLiveData<CalendarEventUIState>()
    val calendarEvent : LiveData<CalendarEventUIState> get()=_calendarEvent

    private val _isUpdate = MutableLiveData<Boolean>(false)
    val isUpdate : LiveData<Boolean> get() = _isUpdate

    var calId : Long = 0L
    var eventId:Long = 0L
    var title : String = ""
    var dTStart: String = ""
    var dTEnd: String = ""
    var description :String = ""
    var baseTime:String = ""
    var timeZone:String = ""


    fun updateCalendarEvent() {
        val new = CalendarEventUIState(calId = calId,
            eventId = eventId,
            title = title,
            dTStart = dTStart.toLong(),
            dTEnd = dTEnd.toLong(),
            description = description,
            baseTime =baseTime,
            timeZone = timeZone)

        viewModelScope.launch {
            updateCalendarEventUseCase(new)
        }
        onUpdate()
    }

    fun deleteCalendarEvent(calendarEvent: CalendarEventUIState) {
        viewModelScope.launch {
            deleteCalendarEventUseCase(calendarEvent)
        }
    }

    fun onUpdate() {
        _isUpdate.value = !isUpdate.value!!
    }


    fun onTextChanged(s: CharSequence, start:Int, before:Int, count:Int, tag:UpdateTag) {
        when(tag) {
            UpdateTag.TITLE -> title = s.toString()
            UpdateTag.DTSTART -> dTStart = s.toString()
            UpdateTag.DTEND -> dTEnd = s.toString()
            UpdateTag.DESCRIPTION-> description = s.toString()
            UpdateTag.BASETIME-> baseTime = s.toString()
        }
    }

    fun setCalendarEvent(calendarEvent: CalendarEventUIState) {
        _calendarEvent.value = calendarEvent

        calId = calendarEvent.calId
        eventId = calendarEvent.eventId
        title = calendarEvent.title
        dTStart = calendarEvent.dTStart.toString()
        dTEnd =  calendarEvent.dTEnd.toString()
        description = calendarEvent.description
        baseTime = calendarEvent.baseTime
        timeZone = calendarEvent.timeZone
    }

}