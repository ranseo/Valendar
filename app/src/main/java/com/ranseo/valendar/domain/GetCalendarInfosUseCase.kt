package com.ranseo.valendar.domain

import com.ranseo.valendar.data.repo.CalendarInfoRepository
import com.ranseo.valendar.data.model.Result
import javax.inject.Inject

class GetCalendarInfosUseCase @Inject constructor(calendarInfoRepository: CalendarInfoRepository) {
    val calendarInfoGetter : suspend ()-> Result<Any> = {
        calendarInfoRepository.queryCalendarInfo()
    }

    suspend operator fun invoke() : Result<Any> = calendarInfoGetter()

}