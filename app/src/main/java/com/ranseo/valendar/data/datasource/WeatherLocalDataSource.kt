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


    /**
     * 만약 해당 baseDate와 baseTime을 가진 테이블이 현재 local database에 없다면 0을 리턴, 그렇지 않다면 1이상의 수 리턴.
     */
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