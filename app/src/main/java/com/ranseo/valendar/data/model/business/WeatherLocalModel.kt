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
    @ColumnInfo(name = "base_time")
    val baseTime: Int,
    @PrimaryKey
    @ColumnInfo(name = "category")
    val category: String,
    @ColumnInfo(name = "fcst_date")
    val fcstDate : Int,
    @ColumnInfo(name = "fcst_time")
    val fcstTime : Int,
    @ColumnInfo(name = "fcst_value")
    val fcstValue : String,
    @ColumnInfo(name = "nx")
    val nx : Int,
    @ColumnInfo(name = "ny")
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