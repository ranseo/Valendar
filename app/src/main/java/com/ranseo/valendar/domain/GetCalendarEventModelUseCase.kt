package com.ranseo.valendar.domain

import com.ranseo.valendar.data.model.business.CalendarEventLocalModel
import com.ranseo.valendar.data.repo.CalendarEventRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCalendarEventModelUseCase @Inject constructor(calendarEventRepository: CalendarEventRepository) {
    val calendarEventGetter : (start:Long, end:Long) -> Flow<List<CalendarEventLocalModel>> = { start, end ->
        calendarEventRepository.queryCalendarEventLocalModel(start,end)
    }



    operator fun invoke(start:Long, end:Long) : Flow<List<CalendarEventLocalModel>> = calendarEventGetter(start, end)

}