package com.ranseo.valendar.data.model.business

import WeatherRemoteModel
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather_table", primaryKeys = ["base_date","base_time","category"])
data class WeatherLocalModel(
    @ColumnInfo(name = "base_date")
    val baseDate: Int,
    @ColumnInfo(name = "base_time")
    val baseTime: Int,
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
//    constructor(_baseDate: Int,  _baseTime: Int, _category: String, _fcstDate: Int, _fcstTime: Int, _fcstValue: String, _nx: Int , _ny: Int) : this(
//        baseDate = _baseDate,
//        baseTime = _baseTime,
//        category = _category,
//        fcstDate = _fcstDate,
//        fcstTime = _fcstTime,
//        fcstValue = _fcstValue,
//        nx = _nx,
//        ny = _ny
//    )
}

data class WeatherLocalModelContainer(
    val weathers : List<WeatherLocalModel>
) {
    companion object {
        fun getWeathers(weathers: List<WeatherRemoteModel.Item>) : WeatherLocalModelContainer{
            return WeatherLocalModelContainer(weathers.map{WeatherLocalModel(it.baseDate, it.baseTime, it.category, it.fcstTime, it.fcstTime, it.fcstValue, it.nx, it.ny)})
        }
    }
}