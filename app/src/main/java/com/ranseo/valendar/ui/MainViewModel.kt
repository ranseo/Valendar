package com.ranseo.valendar.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.work.*
import com.ranseo.valendar.WEATHER_WORK_UNIQUE_ID
import com.ranseo.valendar.WORKER_KEY_GRID
import com.ranseo.valendar.data.Event
import com.ranseo.valendar.data.model.business.CalendarEvent
import com.ranseo.valendar.data.model.ui.WeatherUIState
import com.ranseo.valendar.domain.GetCalendarInfosUseCase
import com.ranseo.valendar.domain.GetWeatherUseCase
import com.ranseo.valendar.domain.WriteCalendarEventUseCase
import com.ranseo.valendar.worker.WeatherWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import com.ranseo.valendar.data.model.Result
import com.ranseo.valendar.data.model.business.CalendarInfo

@HiltViewModel
class MainViewModel @Inject constructor(
    application: Application,
    val getWeatherUseCase: GetWeatherUseCase,
    val writeCalendarEventUseCase: WriteCalendarEventUseCase,
    val getCalendarInfosUseCase: GetCalendarInfosUseCase
) : AndroidViewModel(application) {

    private val _address = MutableLiveData<String>()
    val address: LiveData<String> get() = _address

    private val _gridLocation = MutableLiveData<Pair<String, String>>()
    val gridLocation: LiveData<Pair<String, String>> get() = _gridLocation

    private val _weather = MutableLiveData<WeatherUIState>()
    val weather: LiveData<WeatherUIState> get() = _weather

    private val _dataSyncBtn = MutableLiveData<Event<Any?>>()
    val dataSyncBtn: LiveData<Event<Any?>> get() = _dataSyncBtn


    fun requestWeatherInfo(grid: Pair<String, String>) {
        val workManager = WorkManager.getInstance(getApplication())

//        val workWeather = PeriodicWorkRequestBuilder<WeatherWorker>(3,
//            TimeUnit.HOURS, 50, TimeUnit.MINUTES)
//            .setInputData(workDataOf(WORKER_KEY_GRID to "${grid.first}//${grid.second}"))
//            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, WorkRequest.MIN_BACKOFF_MILLIS, TimeUnit.MILLISECONDS)
//            .build()
//
//        WorkManager.getInstance(getApplication()).enqueueUniquePeriodicWork(
//            WEATHER_WORK_UNIQUE_ID, // 고유 작업 아이디
//            ExistingPeriodicWorkPolicy.KEEP, //해당 작업이 있을 때 아무것도 하지않기.
//            workWeather
//        )

        val workWeather =
            PeriodicWorkRequestBuilder<WeatherWorker>(15, TimeUnit.MINUTES, 15, TimeUnit.MINUTES)
                .setInputData(workDataOf(WORKER_KEY_GRID to "${grid.first}//${grid.second}"))
                .build()

        workManager.cancelUniqueWork(WEATHER_WORK_UNIQUE_ID)

        workManager.enqueueUniquePeriodicWork(
            WEATHER_WORK_UNIQUE_ID,
            ExistingPeriodicWorkPolicy.KEEP,
            workWeather
        )


        //WorkManager.getInstance(getApplication()).cancelAllWork()
    }

    fun getWeather(
        baseDate: Int,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            _weather.postValue(getWeatherUseCase(baseDate)!!)
        }
    }

    fun setGridLocation(p: Pair<String, String>) {
        _gridLocation.value = p
    }

    fun setAddress(addr: String) {
        _address.postValue("위치 : ${addr}")
    }

    fun onDataSync() {
        _dataSyncBtn.value = Event(Unit)
    }

    fun writeCalendarEvent() {
        val weather = weather.value
        weather?.let {
            viewModelScope.launch {
                val calendarEvent = CalendarEvent.getCalendarEventFromWeather(it)
                writeCalendarEventUseCase(calendarEvent)
            }
        }
    }

    fun getCalendarInfo() {
        viewModelScope.launch {
            when(getCalendarInfosUseCase()) {
//                is Result.Success<String> -> {
//
//                }
//                is Result.Success<List<CalendarInfo>> ->{}
//                is Result.Error-> {}
//                else ->{}
            }
        }
    }

    companion object {
        private const val TAG = "MainViewModel"
    }

}