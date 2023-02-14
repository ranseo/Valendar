package com.ranseo.valendar.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import com.ranseo.valendar.data.model.Result
import com.ranseo.valendar.data.model.ui.WeatherUIState
import com.ranseo.valendar.domain.GetWeatherUseCase
import com.ranseo.valendar.util.Log
import com.ranseo.valendar.util.LogTag
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    application: Application,
    val getWeatherUseCase: GetWeatherUseCase
) : AndroidViewModel(application) {

    private val _weatherUIState = MutableLiveData<WeatherUIState>()
    val weatherUIState : LiveData<WeatherUIState>
        get() = _weatherUIState

    val pop = Transformations.map(weatherUIState) {
        "강수확률 : ${it.pop}%"
    }
    val pty = Transformations.map(weatherUIState) {
        "강수형태 : ${when(it.pty) {
            1 -> "비"
            2 -> "비/눈"
            3 -> "눈"
            4-> "소나기"
            else -> "없음"
        }}"
    }

    val tmp = Transformations.map(weatherUIState) {
        "현재기온 : ${it.tmp}ºC"
    }

    val sky = Transformations.map(weatherUIState) {
        "오늘날씨 : ${when(it.sky) {
            1 -> "맑음"
            3 -> "구름많음"
            4 -> "흐림"
            else -> "애매모호함"
        }}"
    }

    fun getWeather(
        baseDate: String,
        baseTime: String,
        nx: String,
        ny: String,
        numOfRows: Int = 14,
        pageNo: Int = 1,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            getWeatherUseCase(numOfRows, pageNo, baseDate, baseTime, nx, ny).collect { result ->
                when (result) {
                    is Result.Success<Weather.Items> -> {
                        Log.log(TAG, "getWeatherUseCase success :${result.data}", LogTag.I)
                        _weatherUIState.postValue(WeatherUIState.getWeatherUIStateFromItem(result.data.item))
                    }
                    is Result.Error -> {
                        Log.log(TAG, "getWeatherUseCase success :${result.exception}", LogTag.I)
                        _weatherUIState.postValue(WeatherUIState.getWeatherUIStateFromItem(listOf()))
                    }
                }
            }
        }
    }
    companion object {
        private const val TAG = "MainViewModel"
    }

}