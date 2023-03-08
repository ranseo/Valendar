package com.ranseo.valendar.domain

import android.os.Build
import androidx.annotation.RequiresApi
import com.ranseo.valendar.data.model.ui.CalendarEventUIState
import com.ranseo.valendar.data.model.ui.asLocalModel
import com.ranseo.valendar.data.repo.CalendarEventRepository
import javax.inject.Inject

class UpdateCalendarEventUseCase @Inject constructor(private val calendarEventRepository: CalendarEventRepository) {

    @RequiresApi(Build.VERSION_CODES.R)
    private val calendarEventUpdater : suspend (calendarEvent: CalendarEventUIState) -> Unit = {
        calendarEventRepository.updateCalendarEvent(it.asLocalModel())
    }

    suspend operator fun invoke(calendarEvent: CalendarEventUIState) {
        calendarEventUpdater(calendarEvent)
    }
}