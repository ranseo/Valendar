package com.ranseo.valendar.data.model.ui

import WeatherRemoteModel
import com.ranseo.valendar.data.model.business.WeatherLocalModel

data class WeatherUIState(
    val tmp: String,
    val pop: String,
    val pty: String,
    val sky: String,
    val fcstTime: String
) {


    companion object {
        fun getWeatherUIStateFromItem(items: List<WeatherLocalModel>): WeatherUIState {
            var tmp: String = ""
            var pop = ""
            var pty = ""
            var sky = ""
            val fcstTime: String = if (items.isNotEmpty()) {
                run {
                    val time = ("%04d".format(items[0].fcstTime))
                    val hour = time.substring(0, 2).toInt()
                    val min = time.substring(2, 4).toInt()
                    "기상 예보 시각 : %02d시 %02d분".format(hour, min)
                }
            } else ""

            for (item in items) {
                when (item.category) {
                    "TMP" -> {
                        tmp = convertTmp(item.fcstValue)
                    }
                    "POP" -> {
                        pop = convertPop(item.fcstValue)
                    }
                    "PTY" -> {
                        pty = convertPty(item.fcstValue)
                    }
                    "SKY" -> {
                        sky = convertSky(item.fcstValue)
                    }
                    else -> {}
                }
            }

            return WeatherUIState(tmp, pop, pty, sky, fcstTime)
        }

        private fun convertTmp(tmp: String): String = "현재기온 : ${tmp}ºC"
        private fun convertPop(pop: String): String = "강수확률 : ${pop}%"
        private fun convertPty(pty: String): String = "강수형태 : ${
            when (pty) {
                "1" -> "비"
                "2" -> "비/눈"
                "3" -> "눈"
                "4" -> "소나기"
                else -> "없음"
            }
        }"

        private fun convertSky(sky: String): String = "오늘날씨 : ${
            when (sky) {
                "1" -> "맑음"
                "3" -> "구름많음"
                "4" -> "흐림"
                else -> "애매모호함"
            }
        }"

    }
}
