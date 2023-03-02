package com.ranseo.valendar.domain

import com.ranseo.valendar.data.repo.CalendarInfoRepository
import com.ranseo.valendar.data.model.Result
import com.ranseo.valendar.data.model.business.CalendarInfo
import javax.inject.Inject

class GetCalendarInfosUseCase @Inject constructor(calendarInfoRepository: CalendarInfoRepository) {
    val calendarInfoGetter : suspend ()-> Result<List<CalendarInfo>> = {
        calendarInfoRepository.queryCalendarInfo()
    }

    suspend operator fun invoke() : Result<List<CalendarInfo>> = calendarInfoGetter()

}