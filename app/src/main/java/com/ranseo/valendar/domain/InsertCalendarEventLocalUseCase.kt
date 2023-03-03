package com.ranseo.valendar.domain

import com.ranseo.valendar.data.model.business.CalendarEventCPModel
import com.ranseo.valendar.data.repo.CalendarEventRepository
import javax.inject.Inject
import com.ranseo.valendar.data.model.Result
import com.ranseo.valendar.data.model.business.CalendarEventLocalModel

class InsertCalendarEventLocalUseCase @Inject constructor(private val calendarEventRepository: CalendarEventRepository) {

    private val calendarEventWriter : suspend (calendarEventLocalModel:CalendarEventLocalModel)-> Unit = {
        calendarEventRepository.insertCalendarEventLocalModel(it)
    }

    suspend operator fun invoke(calendarEventLocalModel:CalendarEventLocalModel)  {
        return calendarEventWriter(calendarEventLocalModel)
    }
}