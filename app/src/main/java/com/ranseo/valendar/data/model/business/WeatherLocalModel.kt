package com.ranseo.valendar.data.model.business

import WeatherRemoteModel
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather_table")
data class WeatherLocalModel(
    @PrimaryKey
    @ColumnInfo(name = "base_date")
    val baseDate: Int,
    val baseTime: Int,
    @PrimaryKey
    val category: String,
    val fcstDate : Int,
    val fcstTime : Int,
    val fcstValue : String,
    val nx : Int,
    val ny : Int
) {
    constructor(weather: WeatherRemoteModel.Item) : this(
        baseDate = weather.baseDate,
        baseTime = weather.baseTime,
        category = weather.category,
        fcstDate = weather.fcstDate,
        fcstTime = weather.fcstTime,
        fcstValue = weather.fcstValue,
        nx = weather.nx,
        ny = weather.ny
    )
}

data class WeatherLocalModelContainer(
    val weathers : List<WeatherLocalModel>
) {
    companion object {
        fun getWeathers(weathers: List<WeatherRemoteModel.Item>) : WeatherLocalModelContainer{
            return WeatherLocalModelContainer(weathers.map{WeatherLocalModel(it)})
        }
    }
}