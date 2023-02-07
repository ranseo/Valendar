package com.ranseo.valendar.network

import retrofit2.http.GET
import retrofit2.http.Path

object NetworkURL {
    const val WEATHER_FORECAST_BASE_URL = "http://apis.data.go.kr/1360000/VilageFcstMsgService/"
    const val WEATHER_LANDFCST_BASE_URL = "http://apis.data.go.kr/1360000/VilageFcstMsgService/getLandFcst/"
}
interface WeatherApiService {
    @GET(WTHR_SITUATION)
    suspend fun getWthrSituation() {

    }

    @GET(LAND_FCST)
    suspend fun getLandFcst() {

    }

    companion object {
        const val WTHR_SITUATION = "getWthrSituation"
        const val LAND_FCST = "getLandFcst"
    }
}