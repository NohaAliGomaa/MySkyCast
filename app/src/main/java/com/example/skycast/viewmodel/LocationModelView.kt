package com.example.skycast.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.skycast.model.location.LocationHelper
import com.example.skycast.model.location.LocationState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class LocationViewModel(private val repository: LocationHelper) : ViewModel() {

    private val _locationState = MutableStateFlow<LocationState>(LocationState.Loading)
    val locationState: StateFlow<LocationState> = _locationState

    fun fetchCurrentLocation() {
        _locationState.value = LocationState.Loading
        viewModelScope.launch {
            try {
                val location = repository.getCurrentLocation()
                _locationState.value = LocationState.Success(location)
            } catch (e: Exception) {
                _locationState.value = LocationState.Error(e.message ?: "Unknown error")
            }
        }
    }
}
class LocationViewModelFactory(private val repo: LocationHelper) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LocationViewModel(repo) as T
    }
}