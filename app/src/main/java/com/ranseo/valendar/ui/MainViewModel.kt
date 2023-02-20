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

    private val _address = MutableLiveData<String>()
    val address : LiveData<String>
        get() = _address

    private val _gridLocation = MutableLiveData<Pair<Int,Int>>((0 to 0))
    val gridLocation : LiveData<Pair<Int,Int>>
        get() = _gridLocation

    private val _weather = MutableLiveData<WeatherUIState>()
    val weather : LiveData<WeatherUIState>
        get() = _weather

    val pop = Transformations.map(weather) {
        "강수확률 : ${it.pop}%"
    }
    val pty = Transformations.map(weather) {
        "강수형태 : ${when(it.pty) {
            1 -> "비"
            2 -> "비/눈"
            3 -> "눈"
            4-> "소나기"
            else -> "없음"
        }}"
    }

    val tmp = Transformations.map(weather) {
        "현재기온 : ${it.tmp}ºC"
    }

    val sky = Transformations.map(weather) {
        "오늘날씨 : ${when(it.sky) {
            1 -> "맑음"
            3 -> "구름많음"
            4 -> "흐림"
            else -> "애매모호함"
        }}"
    }

    fun getWeather(
        baseDate: Int,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
             _weather.postValue(getWeatherUseCase(baseDate))
        }
    }

    fun setGridLocation(p:Pair<Int,Int>) {
        _gridLocation.value = p
    }

    fun setAddress(addr:String) {
        _address.postValue("위치 : ${addr}")
    }

    companion object {
        private const val TAG = "MainViewModel"
    }

}