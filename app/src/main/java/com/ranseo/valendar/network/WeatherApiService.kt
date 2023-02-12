package com.ranseo.valendar.network

import Weather
import com.ranseo.valendar.BuildConfig
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

object NetworkURL {
    const val WEATHER_FORECAST_BASE_URL = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/"
}
interface WeatherApiService {
    @GET("getVilageFcst?serviceKey=${BuildConfig.WEATHER_FCST_KEY}")
    suspend fun getWeather(
        @Query("numOfRows") numOfRows : Int,
        @Query("pageNo") pageNo : Int,
        @Query("base_date") baseDate : Int,
        @Query("base_time") baseTime : Int,
        @Query("nx") nx : String,
        @Query("ny") ny : String,
        @Query("dataType") dataType : String ="JSON"
    ) : Response<Weather>
    companion object {
        const val LAND_FCST = "getVilageFcst"
    }
}