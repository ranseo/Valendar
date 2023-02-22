package com.ranseo.valendar.util

import java.lang.Math.pow
import kotlin.math.*

class LocationConverter {
    fun convertLLToXY(lon: Float, lat: Float): Pair<String, String> {
        val map: LamcParameter = LamcParameter()
        val floats1: Array<Float> = Array(4) { 0f }  //1.lon 2.lat 3.x 4.y

        floats1[0] = lon
        floats1[1] = lat

        getMapConv(floats1,0,map)

        Log.log(TAG, "convertLLToXY : lon = ${floats1[0]}, lat = ${floats1[1]} ==>> x = ${floats1[2].toInt()}, y = ${floats1[3].toInt()}", LogTag.I)

        return (floats1[2].toInt().toString() to floats1[3].toInt().toString())
    }

    data class LamcParameter(
        val re: Double = 6371.00877, // 사용할 지구 반경
        val grid: Double = 5.0, // 격자 간격
        val slat1: Double = 30.0, //표준 위도
        val slat2: Double = 60.0, //표준 위도
        val olon: Double = 126.0, //기준점의 경도
        val olat: Double = 38.0, //기준점의 위도
        val xo: Double = 210 / grid, //기준점의 X좌표
        val yo: Double = 675 / grid, // 기준점의 y좌표
        var first: Int = 0 //시작여부 (0==시작)
    )


    private fun getMapConv(floats1: Array<Float>, code: Int, map: LamcParameter) {

        val floats2: Array<Float> = Array(4) { 0f } //1.lon 2.lat 3.x 4.y

        if (code == 0) {
            floats2[0] = floats1[0]
            floats2[1] = floats1[1]
            lamcproj(floats2, code, map)
            floats1[2] = floats2[2] + 1.5f
            floats1[3] = floats2[3] + 1.5f
        }
    }

    //1.lon 2.lat 3.x 4.y
    private fun lamcproj(floats2: Array<Float>, code: Int, map: LamcParameter) {
        var PI: Double = 0.0
        var DEGRAD: Double = 0.0
        var RADDEG: Double = 0.0

        var re: Double = 0.0
        var olon: Double = 0.0
        var olat: Double = 0.0
        var sn: Double = 0.0
        var sf: Double = 0.0
        var ro: Double = 0.0

        var slat1: Double = 0.0
        var slat2: Double = 0.0
        var alon: Double = 0.0
        var alat: Double = 0.0
        var xn: Double = 0.0
        var yn: Double = 0.0
        var ra: Double = 0.0
        var theta: Double = 0.0

        if (map.first == 0) {
            PI = asin(1.0) * 2.0
            DEGRAD = PI / 180.0
            RADDEG = 180.0 / PI

            re = map.re / map.grid
            slat1 = map.slat1 * DEGRAD
            slat2 = map.slat2 * DEGRAD
            olon = map.olon * DEGRAD
            olat = map.olat * DEGRAD

            sn = tan(PI * 0.25 + slat2 * 0.5) / tan(PI * 0.25 + slat1 * 0.5)
            sn = ln(cos(slat1) / cos(slat2)) / ln(sn)
            sf = tan(PI * 0.25 + slat1 * 0.5)
            sf = pow(sf, sn)
            ro = tan(PI * 0.25 + olat * 0.5)
            ro = re * sf / pow(ro, sn)
            map.first = 1
        }

        //1.lon 2.lat 3.x 4.y
        if (code == 0) {
            ra = tan(PI * 0.25 + floats2[1] * DEGRAD * 0.5)
            ra = re * sf / pow(ra, sn)
            theta = floats2[0] * DEGRAD - olon
            if (theta > PI) theta -= 2.0 * PI
            if (theta < -PI) theta += 2.0 * PI
            theta *= sn
            floats2[2] = ((ra * sin(theta)) + map.xo).toFloat()
            floats2[3] = ((ro - ra * cos(theta)) + map.yo).toFloat()
        }
    }


    companion object {
        private const val TAG = "LocationConverter"
        private const val NX = 149
        private const val NY = 253
    }
}