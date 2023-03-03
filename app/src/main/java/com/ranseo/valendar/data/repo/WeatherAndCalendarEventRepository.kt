package com.ranseo.valendar.data.repo

import com.ranseo.valendar.data.model.Result
import com.ranseo.valendar.data.model.business.WeatherLocalModelContainer
import com.ranseo.valendar.util.Log
import com.ranseo.valendar.util.LogTag
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class WeatherAndCalendarEventRepository @Inject constructor(private val weatherRepository:WeatherRepository, private val calendarEventRepository: CalendarEventRepository) {

    suspend fun getWeatherRemoteModel(
        numOfRows: Int,
        pageNo: Int,
        baseDate: String,
        baseTime: String,
        nx: String,
        ny: String
    ) = withContext(Dispatchers.IO) {
        when(val result = weatherRepository.getWeatherRemoteModel(numOfRows,pageNo,baseDate,baseTime,nx,ny)) {
            is Result.Success<WeatherRemoteModel.Items> -> {
                Log.log(TAG, "getWeatherUseCase success :${result.data}", LogTag.I)
                val list = WeatherLocalModelContainer.getWeathers(result.data.item).weathers

                if(list.isNotEmpty()) {
                    when(weatherRepository.queryCountOfWeather(list[0].baseDate, list[0].baseTime)) {
                        0 -> {
                            weatherRepository.insertWeatherLocalModel(list)

                            //weatherUIState를 이용해서 CalendarEventCPModel 을 만든 뒤, 집어넣어줘야해.
                            calendarEventRepository.insertCPModelAndThenLocalModel()
                        }
                        else -> {

                        }
                    }
                }

            }

            is Result.Error -> {
                Log.log(TAG, "getWeatherUseCase success :${result.exception}", LogTag.I)
            }
        }
    }

    companion object {
        private const val TAG = "WeatherAndCalendarEventRepository"
    }
}