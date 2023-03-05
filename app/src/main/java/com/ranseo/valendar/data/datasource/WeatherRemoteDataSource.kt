package com.ranseo.valendar.data.datasource

import WeatherRemoteModel
import com.ranseo.valendar.data.model.Result
import com.ranseo.valendar.data.repo.WeatherRepository
import com.ranseo.valendar.network.WeatherApiService
import com.ranseo.valendar.util.Log
import com.ranseo.valendar.util.LogTag
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import okhttp3.Dispatcher
import javax.inject.Inject

class WeatherRemoteDataSource @Inject constructor(private val weatherApiService: WeatherApiService) {
    suspend fun getWeather(
        numOfRows: Int,
        pageNo: Int,
        baseDate: String,
        baseTime: String,
        nx: String,
        ny: String
    ) : Result<WeatherRemoteModel.Items> = withContext(Dispatchers.IO) {
        try {
            val weather =
                weatherApiService.getWeather(numOfRows, pageNo, baseDate, baseTime, nx, ny)


            val isNull = weather.body() != null
            val resultCode = weather.body()?.response?.header?.resultCode
            val resultMsg = weather.body()?.response?.header?.resultMsg
            val items = weather.body()?.response?.body?.items

            if (isNull && resultCode == "%02d".format(0).toInt()) {
                Result.Success(items!!)
            } else {
                Result.Error(java.lang.Exception("ErrorCode : ${resultCode} - ${resultMsg}"))
            }

        } catch (error: Exception) {
            Log.log(TAG, "getWeather() error : ${error.message}", LogTag.I)
            Result.Error(error)
        }
    }


    companion object {
        private val TAG = "WeatherRemoteDataSource"
    }
}