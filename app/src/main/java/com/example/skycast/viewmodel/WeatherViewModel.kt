package com.example.skycast.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.skycast.model.repositries.WeatherRepositry
import com.example.skycast.model.result.WeatherResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch


class WeatherViewModel(private val repo :WeatherRepositry) : ViewModel() {
    private val _currentWeather = MutableStateFlow<WeatherResult>(WeatherResult.Loading)
    val weather: StateFlow<WeatherResult> = _currentWeather.asStateFlow()
//
//    private val _favProducts = MutableStateFlow<WeatherResult>(WeatherResult.Loading)
//    val favProducts: StateFlow<WeatherResult>  = _favProducts.asStateFlow()

    fun getCurrentWeather() {
        viewModelScope.launch{
            repo.getCurrentWeather()
                .catch { e -> _currentWeather.value =  WeatherResult.Failure(e) }
                .collect{ currentWeather ->
                    Log.i("TAG", "CurrentWeathers loaded: ${currentWeather.current}")
                    _currentWeather.value = WeatherResult.Success(currentWeather)
                }
        }
    }
}
class WeatherFactory(val repo: WeatherRepositry): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if(modelClass.isAssignableFrom(WeatherViewModel::class.java)){
            WeatherViewModel(repo) as T
        }
        else{
            throw IllegalArgumentException("View Model Calss not found")
        }
    }
}