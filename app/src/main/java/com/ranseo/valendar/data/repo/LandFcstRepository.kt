package com.ranseo.valendar.data.repo

import com.ranseo.valendar.BuildConfig
import com.ranseo.valendar.network.WeatherApiService
import com.ranseo.valendar.util.Log
import com.ranseo.valendar.util.LogTag
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LandFcstRepository @Inject constructor(private val weatherApiService: WeatherApiService) {
    suspend fun getLandFcst(numOfRows:Int, pageNo:Int, baseDate:Int, baseTime:Int, nx:String, ny:String) {
        withContext(Dispatchers.IO) {
            try {
                val landFcst = weatherApiService.getWeather( numOfRows, pageNo, baseDate,baseTime , nx,ny)
                Log.log(TAG, "getLandFcst() success :${landFcst.body()?.response?.body?.items?.item}", LogTag.I)
            } catch (error: Exception) {
                Log.log(TAG, "getLandFcst() error : ${error.message}", LogTag.I)
            }

        }
    }

    companion object {
        private val TAG = "LandFcstRepository"
    }
}