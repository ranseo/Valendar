package com.ranseo.valendar.domain

import com.ranseo.valendar.data.model.business.CalendarEventModel
import com.ranseo.valendar.data.repo.CalendarEventRepository
import javax.inject.Inject

class WriteCalendarEventUseCase @Inject constructor(private val calendarEventRepository: CalendarEventRepository) {

    private val calendarEventWriter : suspend (calendarEventModel:CalendarEventModel)-> com.ranseo.valendar.data.model.Result<Any> = {
        calendarEventRepository.insertCalendarEvent(it)
    }

    suspend operator fun invoke(calendarEventModel: CalendarEventModel) : com.ranseo.valendar.data.model.Result<Any> {
        return calendarEventWriter(calendarEventModel)
    }
}