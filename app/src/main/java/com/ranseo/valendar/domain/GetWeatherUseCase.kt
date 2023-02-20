package com.ranseo.valendar.domain

import androidx.lifecycle.LiveData
import com.ranseo.valendar.data.model.Result
import com.ranseo.valendar.data.model.ui.WeatherUIState
import com.ranseo.valendar.data.repo.WeatherRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetWeatherUseCase @Inject constructor(repository: WeatherRepository) {
    val weatherUIStateGetter: suspend (baseDate:Int) -> WeatherUIState =
        { baseDate->
            repository.getWeatherUIState(baseDate)
        }

    suspend operator fun invoke(baseDate: Int) : WeatherUIState {
        return weatherUIStateGetter(baseDate)
    }
}