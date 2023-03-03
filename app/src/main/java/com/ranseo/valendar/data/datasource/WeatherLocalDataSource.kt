package com.ranseo.valendar.data.datasource

import WeatherRemoteModel
import androidx.lifecycle.LiveData
import com.ranseo.valendar.data.model.business.WeatherLocalModel
import com.ranseo.valendar.room.ValendarDao
import javax.inject.Inject

class WeatherLocalDataSource @Inject constructor(private val valendarDao: ValendarDao) {


    suspend fun queryWeather(baseDate:Int) : List<WeatherLocalModel>{
        return valendarDao.getWeather(baseDate)
    }

    suspend fun queryCountOfWeather(baseDate: Int, baseTime: Int): Int {
        return valendarDao.getCountOfWeather(baseDate, baseTime)
    }


    suspend fun insertWeather(weather: WeatherLocalModel) {
        valendarDao.insert(weather)
    }

    suspend fun insertWeathers(weathers: List<WeatherLocalModel>) {
        valendarDao.insert(weathers)
    }
}