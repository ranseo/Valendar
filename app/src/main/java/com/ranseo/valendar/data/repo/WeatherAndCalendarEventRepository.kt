package com.ranseo.valendar.data.repo

import com.ranseo.valendar.data.model.Result
import com.ranseo.valendar.data.model.business.CalendarEventCPModel
import com.ranseo.valendar.data.model.business.CalendarEventLocalModel
import com.ranseo.valendar.data.model.business.WeatherLocalModelContainer
import com.ranseo.valendar.data.model.ui.WeatherUIState
import com.ranseo.valendar.util.Log
import com.ranseo.valendar.util.LogTag
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class WeatherAndCalendarEventRepository @Inject constructor(private val weatherRepository:WeatherRepository, private val calendarEventRepository: CalendarEventRepository) {

    /**
     * WeatherRemoteModel에 대해서 API 요청을 통해 데이터를 GET 한 후,
     * WeatherLocalModel 로 변환하여 Local Database에 Insert.
     * 만약 해당 WeatherLocalModel과 같은 테이블이 Local Database에 없다면, (=해당 데이터가 처음으로 local database에 insert된다면)
     * Weather Data는 CalendarEventCPModel로 변환되어 CP & Local 에 각각 Insert
     * */
    suspend fun getWeatherRemoteAndInsertWeatherLocalAndThenInsertCalendarEventBusiness(
        numOfRows: Int,
        pageNo: Int,
        baseDate: String,
        baseTime: String,
        nx: String,
        ny: String
    ) : Result<CalendarEventLocalModel> = withContext(Dispatchers.IO) {
        when(val result = weatherRepository.getWeatherRemoteModel(numOfRows,pageNo,baseDate,baseTime,nx,ny)) {
            is Result.Success<WeatherRemoteModel.Items> -> {
                Log.log(TAG, "getWeatherUseCase success :${result.data}", LogTag.I)
                val list = WeatherLocalModelContainer.getWeathers(result.data.item).weathers

                if(list.isNotEmpty()) {
                    when(weatherRepository.queryCountOfWeather(list[0].baseDate, list[0].baseTime)) { //insert와 query가 중복되는 것을 방지.
                        0 -> {
                            //WeatherLocalModel insert
                            weatherRepository.insertWeatherLocalModel(list)

                            //weatherUIState 인스턴스 구현 -> CalendarEventCPModel 인스턴스 구현 -> CP & Local 로 insert
                            val weatherUIState = WeatherUIState.getWeatherUIStateFromItem(list)
                            val calendarEventCPModel = CalendarEventCPModel.getCalendarEventFromWeather(weatherUIState)
                            when(val res = calendarEventRepository.insertCPAndThenLocal(calendarEventCPModel)) {
                                is Result.Success<CalendarEventLocalModel> -> {
                                    Result.Success(res.data)
                                }
                                is Result.Error -> {
                                    Result.Error(res.exception)
                                }
                            }
                        }
                        else -> {
                            Result.Error(java.lang.Exception("이미 해당 데이터는 추가된 상태 입니다."))
                        }
                    }
                } else {
                    Result.Error(java.lang.Exception("List<WeatherLocalModel>의 원소가 0 개 입니다.\n기상청 API 요청에 문제가 있을 수도 있습니다."))
                }
            }
            is Result.Error -> {
                Result.Error(result.exception)
            }
        }
    }

    companion object {
        private const val TAG = "WeatherAndCalendarEventRepository"
    }
}