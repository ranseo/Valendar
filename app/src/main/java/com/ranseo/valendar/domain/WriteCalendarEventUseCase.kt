package com.ranseo.valendar.domain

import com.ranseo.valendar.data.model.business.CalendarEvent
import com.ranseo.valendar.data.repo.CalendarEventRepository
import javax.inject.Inject

class WriteCalendarEventUseCase @Inject constructor(private val calendarEventRepository: CalendarEventRepository) {

    private val calendarEventWriter : suspend (calendarEvent:CalendarEvent)-> com.ranseo.valendar.data.model.Result<Any> = {
        calendarEventRepository.insertCalendarEvent(it)
    }

    suspend operator fun invoke(calendarEvent: CalendarEvent) : com.ranseo.valendar.data.model.Result<Any> {
        return calendarEventWriter(calendarEvent)
    }
}