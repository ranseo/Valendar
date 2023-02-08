package com.ranseo.valendar.network

import com.ranseo.valendar.data.model.business.LandFcst
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

object NetworkURL {
    const val WEATHER_FORECAST_BASE_URL = "http://apis.data.go.kr/1360000/VilageFcstMsgService/"
}
interface WeatherApiService {
    @GET(WTHR_SITUATION)
    suspend fun getWthrSituation(
        @Query("serviceKey") serviceKey: String,
        @Query("numOfRows") numOfRows: Int,
        @Query("pageNo")  pageNo: Int,
        @Query("stnId") stnId: Int,
        @Query("dataType") dataType:String ="JSON",
    ) {}

    @GET("{whereFcst}")
    suspend fun getLandFcst(
        @Path("whereFcst") whereFcst : String,
        @Query("serviceKey") serviceKey: String,
        @Query("numOfRows") numOfRows: String,
        @Query("pageNo")  pageNo: String,
        @Query("regId") regId:String,
        @Query("dataType") dataType:String ="JSON"
    ) : LandFcst

    companion object {
        const val WTHR_SITUATION = "getWthrSituation"
        const val LAND_FCST = "getLandFcst"
    }
}