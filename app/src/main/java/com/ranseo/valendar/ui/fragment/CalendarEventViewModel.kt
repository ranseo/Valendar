package com.ranseo.valendar.ui.fragment

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ranseo.valendar.data.model.ui.CalendarEventUIState
import com.ranseo.valendar.domain.DeleteCalendarEventUseCase
import com.ranseo.valendar.domain.UpdateCalendarEventUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CalendarEventViewModel @Inject constructor(
    application: Application,
    val updateCalendarEventUseCase: UpdateCalendarEventUseCase,
    val deleteCalendarEventUseCase: DeleteCalendarEventUseCase
) : AndroidViewModel(application) {

    private val _isUpdate = MutableLiveData<Boolean>(false)
    val isUpdate : LiveData<Boolean> get() = _isUpdate



    fun updateCalendarEvent(calendarEvent: CalendarEventUIState) {
        viewModelScope.launch {
            updateCalendarEventUseCase(calendarEvent)
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



}