package com.ranseo.valendar.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.work.*
import com.ranseo.valendar.WEATHER_WORK_UNIQUE_ID
import com.ranseo.valendar.WORKER_KEY_GRID_X
import com.ranseo.valendar.WORKER_KEY_GRID_Y
import com.ranseo.valendar.data.Event
import com.ranseo.valendar.data.model.ui.WeatherUIState
import com.ranseo.valendar.worker.WeatherWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import com.ranseo.valendar.data.model.Result
import com.ranseo.valendar.data.model.business.CalendarInfo
import com.ranseo.valendar.data.model.ui.CalendarEventUIState
import com.ranseo.valendar.domain.*
import com.ranseo.valendar.util.Log
import com.ranseo.valendar.util.LogTag
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@HiltViewModel
class MainViewModel @Inject constructor(
    application: Application,
    val getWeatherUseCase: GetWeatherUseCase,
    val getCalendarInfosUseCase: GetCalendarInfosUseCase,
    val getCalendarEventIUIStateUseCase: GetCalendarEventIUIStateUseCase
) : AndroidViewModel(application) {

    private val _address = MutableLiveData<String>()
    val address: LiveData<String> get() = _address

    private val _gridLocation = MutableLiveData<Pair<String, String>>()
    val gridLocation: LiveData<Pair<String, String>> get() = _gridLocation

    private val _weather = MutableLiveData<WeatherUIState>()
    val weather: LiveData<WeatherUIState> get() = _weather

    private val _dataSyncBtn = MutableLiveData<Event<Any?>>()
    val dataSyncBtn: LiveData<Event<Any?>> get() = _dataSyncBtn

    private val _calendarEvents = MutableStateFlow<List<CalendarEventUIState>>(emptyList())
    val calendarEvents: StateFlow<List<CalendarEventUIState>> = _calendarEvents

    fun requestWeatherInfo(grid: Pair<String, String>) {
        Log.log(TAG,"requestWeaetherInfo : ${grid.toString()}", LogTag.I)
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
            PeriodicWorkRequestBuilder<WeatherWorker>(1, TimeUnit.HOURS, 1, TimeUnit.HOURS)
                .setInputData(workDataOf(WORKER_KEY_GRID_X to grid.first))
                .setInputData(workDataOf(WORKER_KEY_GRID_Y to grid.second))
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

    fun getCalendarInfo() {
        viewModelScope.launch {
            when (val calendarInfos = getCalendarInfosUseCase()) {
                is Result.Success<List<CalendarInfo>> -> {
                    val data = calendarInfos.data
                    if (data.isNotEmpty()) {
                        Log.log(TAG, "getCalendarInfo() Success : ${data}", LogTag.I)
                    } else {
                        Log.log(TAG, "getCalendarInfo() Success, but No Data", LogTag.I)
                    }
                }
                is Result.Error -> {
                    val error = calendarInfos.exception
                    Log.log(TAG, "getCalendarInfo() Failure ${error.message}", LogTag.I)
                }
            }
        }
    }

    fun getCalendarEvent(start: Long, end: Long) {
        viewModelScope.launch {
            getCalendarEventIUIStateUseCase(start, end).collect {
                _calendarEvents.value = it
            }
        }
    }

    companion object {
        private const val TAG = "MainViewModel"
    }

}