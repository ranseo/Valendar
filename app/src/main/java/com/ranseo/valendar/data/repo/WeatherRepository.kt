package com.ranseo.valendar.data.repo

import WeatherRemoteModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.ranseo.valendar.data.datasource.WeatherLocalDataSource
import com.ranseo.valendar.data.datasource.WeatherRemoteDataSource
import com.ranseo.valendar.network.WeatherApiService
import com.ranseo.valendar.util.Log
import com.ranseo.valendar.util.LogTag
import kotlinx.coroutines.flow.Flow
import com.ranseo.valendar.data.model.Result
import com.ranseo.valendar.data.model.business.WeatherLocalModel
import com.ranseo.valendar.data.model.business.WeatherLocalModelContainer
import com.ranseo.valendar.data.model.ui.WeatherUIState
import com.ranseo.valendar.room.ValendarDao
import com.ranseo.valendar.ui.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class WeatherRepository @Inject constructor(private val weatherRemoteDataSource: WeatherRemoteDataSource,
                                            private val weatherLocalDataSource: WeatherLocalDataSource) {

    suspend fun getWeatherUIState(baseDate: Int) : WeatherUIState {
        val localModel : List<WeatherLocalModel>? =  weatherLocalDataSource.getWeather(baseDate)
        return localModel?.let { weather ->
            WeatherUIState.getWeatherUIStateFromItem(weather)
        } ?: WeatherUIState.getWeatherUIStateFromItem(listOf())
    }

    suspend fun getWeatherRemoteModel(
        numOfRows: Int,
        pageNo: Int,
        baseDate: String,
        baseTime: String,
        nx: String,
        ny: String
    ) = withContext(Dispatchers.IO) {
        when(val result = weatherRemoteDataSource.getWeather(numOfRows,pageNo,baseDate,baseTime,nx,ny)) {
            is Result.Success<WeatherRemoteModel.Items> -> {
                Log.log(TAG, "getWeatherUseCase success :${result.data}", LogTag.I)

                val list = WeatherLocalModelContainer.getWeathers(result.data.item).weathers
                weatherLocalDataSource.insertWeathers(list)
            }

            is Result.Error -> {
                Log.log(TAG, "getWeatherUseCase success :${result.exception}", LogTag.I)
                weatherLocalDataSource.insertWeathers(listOf())
            }
        }
    }


    companion object {
        private const val TAG = "WeatherRepository"
    }
}