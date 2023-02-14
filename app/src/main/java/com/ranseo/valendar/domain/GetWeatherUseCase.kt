package com.ranseo.valendar.domain

import com.ranseo.valendar.data.model.Result
import com.ranseo.valendar.data.repo.WeatherRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetWeatherUseCase @Inject constructor(repository: WeatherRepository) {
    val weatherGetter: suspend (numOfRows: Int, pageNo: Int, baseDate: String, baseTime: String, nx: String, ny: String) -> Flow<Result<Weather.Items>> =
        { numOfRows, pageNo, baseDate, baseTime, nx, ny ->
            repository.getWeather(numOfRows, pageNo, baseDate, baseTime, nx, ny)
        }

    suspend operator fun invoke(numOfRows: Int=14, pageNo: Int=1, baseDate: String, baseTime: String, nx: String, ny: String) : Flow<Result<Weather.Items>>{
        return weatherGetter(numOfRows, pageNo, baseDate, baseTime, nx,ny)
    }
}