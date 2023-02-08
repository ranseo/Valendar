package com.ranseo.valendar.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ranseo.valendar.domain.GetLandFcstUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Dispatcher
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    application: Application,
    val getLandFcstUseCase:GetLandFcstUseCase
) : AndroidViewModel(application){

    fun getLandFcst() {
        viewModelScope.launch(Dispatchers.IO) {
            getLandFcstUseCase()
        }
    }

}