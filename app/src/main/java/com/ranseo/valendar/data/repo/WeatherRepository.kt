package com.ranseo.valendar.data.repo

import Weather
import com.ranseo.valendar.network.WeatherApiService
import com.ranseo.valendar.util.Log
import com.ranseo.valendar.util.LogTag
import kotlinx.coroutines.flow.Flow
import com.ranseo.valendar.data.model.Result
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class WeatherRepository @Inject constructor(private val weatherApiService: WeatherApiService) {
    suspend fun getWeather(
        numOfRows: Int,
        pageNo: Int,
        baseDate: String,
        baseTime: String,
        nx: String,
        ny: String
    ): Flow<Result<Weather.Items>> =
        flow {
            try {
                val weather =
                    weatherApiService.getWeather(numOfRows, pageNo, baseDate, baseTime, nx, ny)


                val isNull = weather.body() != null
                val resultCode= weather.body()?.response?.header?.resultCode
                val resultMsg = weather.body()?.response?.header?.resultMsg
                val items = weather.body()?.response?.body?.items

                if(isNull && resultCode == "%02d".format(0).toInt()) {
                    emit(Result.Success(items!!))
                } else {
                    emit(Result.Error(java.lang.Exception("ErrorCode : ${resultCode} - ${resultMsg}")))
                }

            } catch (error: Exception) {
                Log.log(TAG, "getLandFcst() error : ${error.message}", LogTag.I)
                emit(Result.Error(error))
            }
        }


    companion object {
        private val TAG = "WeatherRepository"
    }
}