package com.ranseo.valendar.data.repo

import com.ranseo.valendar.BuildConfig
import com.ranseo.valendar.network.WeatherApiService
import com.ranseo.valendar.util.Log
import com.ranseo.valendar.util.LogTag
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LandFcstRepository @Inject constructor(private val weatherApiService: WeatherApiService) {
    suspend fun getLandFcst() {
        withContext(Dispatchers.IO) {
            try {
                val landFcst = weatherApiService.getLandFcst(BuildConfig.WEATHER_FCST_KEY, 1, 1, "11B20201")
                Log.log(TAG, "getLandFcst() success :${landFcst.toString()}", LogTag.I)
            } catch (error: Exception) {
                Log.log(TAG, "getLandFcst() error : ${error.message}", LogTag.I)
            }

        }
    }

    companion object {
        private val TAG = "LandFcstRepository"
    }
}