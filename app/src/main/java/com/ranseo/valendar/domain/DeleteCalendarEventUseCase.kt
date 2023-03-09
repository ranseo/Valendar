package com.ranseo.valendar.domain

import android.os.Build
import androidx.annotation.RequiresApi
import com.ranseo.valendar.data.model.ui.CalendarEventUIState
import com.ranseo.valendar.data.model.ui.asLocalModel
import com.ranseo.valendar.data.repo.CalendarEventRepository
import javax.inject.Inject
import com.ranseo.valendar.data.model.Result

class DeleteCalendarEventUseCase @Inject constructor(private val calendarEventRepository: CalendarEventRepository) {

    @RequiresApi(Build.VERSION_CODES.R)
    private val calendarEventDeleter : suspend (calendarEvent: CalendarEventUIState) -> Result<Int> = {
        calendarEventRepository.deleteCalendarEvent(it.asLocalModel())
    }

    suspend operator fun invoke(calendarEvent: CalendarEventUIState) : Result<Int> =
        calendarEventDeleter(calendarEvent)

}