package com.ranseo.valendar.room

import androidx.lifecycle.LiveData
import androidx.room.*
import com.ranseo.valendar.data.model.business.WeatherLocalModel

@Dao
interface ValendarDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(weather: WeatherLocalModel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(weathers:List<WeatherLocalModel>)

    @Update
    suspend fun update(weather: WeatherLocalModel)

    @Query("SELECT * FROM weather_table WHERE :baseDate = baseDate")
    suspend fun getWeather(baseDate: Int) : List<WeatherLocalModel>


}