package com.ranseo.valendar.domain

import com.ranseo.valendar.data.model.business.CalendarEventCPModel
import com.ranseo.valendar.data.repo.CalendarEventRepository
import javax.inject.Inject
import com.ranseo.valendar.data.model.Result
import com.ranseo.valendar.data.model.business.CalendarEventLocalModel

class InsertCalendarEventCPUseCase @Inject constructor(private val calendarEventRepository: CalendarEventRepository) {

//    private val calendarEventWriter : suspend (calendarEventCPModel:CalendarEventCPModel)-> Result<CalendarEventLocalModel> = {
//        calendarEventRepository.insertCalendarEventCPModel(it)
//    }
//
//    suspend operator fun invoke(calendarEventCPModel: CalendarEventCPModel) : Result<CalendarEventLocalModel> {
//        return calendarEventWriter(calendarEventCPModel)
//    }
}