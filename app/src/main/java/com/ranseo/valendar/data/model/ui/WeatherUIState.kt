package com.ranseo.valendar.data.model.ui

import WeatherRemoteModel
import com.ranseo.valendar.data.model.business.WeatherLocalModel

data class WeatherUIState(
    val tmp : Float,
    val pop : Float,
    val pty : Int,
    val sky : Int
) {
    companion object {
        fun getWeatherUIStateFromItem(items:List<WeatherLocalModel>) : WeatherUIState {
            var tmp : Float = -1f
            var pop : Float = -1f
            var pty : Int = -1
            var sky : Int = -1
            for(item in items) {
                when(item.category) {
                    "TMP" -> {tmp = item.fcstValue.toFloat()}
                    "POP" -> {pop = item.fcstValue.toFloat()}
                    "PTY" -> {pty = item.fcstValue.toInt()}
                    "SKY" -> {sky = item.fcstValue.toInt()}
                    else -> {}
                }
            }

            return WeatherUIState(tmp,pop,pty,sky)
        }

    }
}
