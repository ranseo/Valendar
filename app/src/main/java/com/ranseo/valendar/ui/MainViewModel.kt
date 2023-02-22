package com.ranseo.valendar.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import androidx.work.*
import com.ranseo.valendar.WEATHER_WORK_UNIQUE_ID
import com.ranseo.valendar.WORKER_KEY_GRID
import com.ranseo.valendar.data.model.Result
import com.ranseo.valendar.data.model.ui.WeatherUIState
import com.ranseo.valendar.domain.GetWeatherUseCase
import com.ranseo.valendar.util.Log
import com.ranseo.valendar.util.LogTag
import com.ranseo.valendar.worker.WeatherWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    application: Application,
    val getWeatherUseCase: GetWeatherUseCase
) : AndroidViewModel(application) {

    private val _address = MutableLiveData<String>()
    val address : LiveData<String>
        get() = _address

    private val _gridLocation = MutableLiveData<Pair<String,String>>()
    val gridLocation : LiveData<Pair<String,String>>
        get() = _gridLocation

    private val _weather = MutableLiveData<WeatherUIState>()
    val weather : LiveData<WeatherUIState>
        get() = _weather


    fun requestWeatherInfo(grid:Pair<String,String>) {
        val workWeather = PeriodicWorkRequestBuilder<WeatherWorker>(3,
            TimeUnit.HOURS, 50, TimeUnit.MINUTES)
            .setInputData(workDataOf(WORKER_KEY_GRID to grid))
            .setBackoffCriteria(BackoffPolicy.LINEAR, WorkRequest.MIN_BACKOFF_MILLIS,TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(getApplication()).enqueueUniquePeriodicWork(
            WEATHER_WORK_UNIQUE_ID, // 고유 작업 아이디
            ExistingPeriodicWorkPolicy.KEEP, //해당 작업이 있을 때 아무것도 하지않기.
            workWeather
        )
    }

    fun getWeather(
        baseDate: Int,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
             _weather.postValue(getWeatherUseCase(baseDate)!!)
        }
    }

    fun setGridLocation(p:Pair<String, String>) {
        _gridLocation.value = p
    }

    fun setAddress(addr:String) {
        _address.postValue("위치 : ${addr}")
    }

    companion object {
        private const val TAG = "MainViewModel"
    }

}