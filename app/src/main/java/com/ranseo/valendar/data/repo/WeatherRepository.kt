package com.ranseo.valendar.data.repo

import WeatherRemoteModel
import android.app.Application
import android.content.ContentResolver
import android.content.ContentValues
import android.net.Uri
import android.provider.CalendarContract
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.ranseo.valendar.ValendarApplication
import com.ranseo.valendar.data.datasource.WeatherLocalDataSource
import com.ranseo.valendar.data.datasource.WeatherRemoteDataSource
import com.ranseo.valendar.network.WeatherApiService
import com.ranseo.valendar.util.Log
import com.ranseo.valendar.util.LogTag
import kotlinx.coroutines.flow.Flow
import com.ranseo.valendar.data.model.Result
import com.ranseo.valendar.data.model.business.WeatherLocalModel
import com.ranseo.valendar.data.model.business.WeatherLocalModelContainer
import com.ranseo.valendar.data.model.ui.WeatherUIState
import com.ranseo.valendar.room.ValendarDao
import com.ranseo.valendar.ui.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class WeatherRepository @Inject constructor(
    private val weatherRemoteDataSource: WeatherRemoteDataSource,
                                            private val weatherLocalDataSource: WeatherLocalDataSource) {


    suspend fun getWeatherUIState(baseDate: Int) : WeatherUIState {
        val localModel : List<WeatherLocalModel>? =  weatherLocalDataSource.queryWeather(baseDate)
        return localModel?.let { weather ->
            WeatherUIState.getWeatherUIStateFromItem(weather)
        } ?: WeatherUIState.getWeatherUIStateFromItem(listOf())
    }


    suspend fun getWeatherRemoteModel(
        numOfRows: Int,
        pageNo: Int,
        baseDate: String,
        baseTime: String,
        nx: String,
        ny: String
    ) : Result<WeatherRemoteModel.Items> = weatherRemoteDataSource.getWeather(numOfRows,pageNo,baseDate,baseTime,nx,ny)

    suspend fun queryCountOfWeather(baseDate: Int, baseTime:Int) : Int = weatherLocalDataSource.queryCountOfWeather(baseDate, baseTime)


    suspend fun insertWeatherLocalModel(weathers: List<WeatherLocalModel>) {
        weatherLocalDataSource.insertWeathers(weathers)
    }




    companion object {
        private const val TAG = "WeatherRepository"
    }
}