package com.example.skycast.viewmodel

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.skycast.model.pojo.Settings
import com.example.skycast.model.pojo.WeatherInfo
import com.example.skycast.model.pojo.WeatherResponse
import com.example.skycast.model.repositries.WeatherRepositry
import com.example.skycast.model.result.LocalDataState
import com.example.skycast.model.result.WeatherResult
import com.example.skycast.model.sharedpreferences.SharedManager
import com.example.skycast.model.util.AppConstants
import com.example.skycast.model.util.NetworkUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch


class WeatherViewModel(
    private val repo: WeatherRepositry,
    private val context: Context
) : ViewModel() {
    private val _currentWeather = MutableStateFlow<WeatherResult>(WeatherResult.Loading)
    val weather: StateFlow<WeatherResult> = _currentWeather.asStateFlow()

    private val _currentWeatherInfo = MutableStateFlow<WeatherInfo>(WeatherInfo(name = "Sorry"))
    val weatherInfo: StateFlow<WeatherInfo> = _currentWeatherInfo.asStateFlow()

    private val _favWeather = MutableStateFlow<LocalDataState>(LocalDataState.Success(emptyList()))
    val favWeather: StateFlow<LocalDataState> = _favWeather.asStateFlow()
//    val setting = SharedManager.getSettings()?:Settings(AppConstants.LANG_EN,
//        false,AppConstants.WEATHER_UNIT)

    fun getCurrentWeather(
        lat: Double,
        lon: Double,
        lang: String,
        units: String
    ) {
        val isOnline = NetworkUtils.isInternetAvailable(context)
        viewModelScope.launch {
            if (isOnline) {
                repo.getCurrentWeather(lat, lon, lang, units, isOnline)
                    .catch { e ->
                        _currentWeather.value = WeatherResult.Failure(e)
                        Log.e("TAG", "Error fetching current weather: ${e.localizedMessage}")
                    }
                    .collect { currentWeather ->
                        if (currentWeather != null && currentWeather.current != null) {
                            Log.i("TAG", "CurrentWeathers loaded: ${currentWeather.current}")
                            _currentWeather.value = WeatherResult.Success(currentWeather)

                            // Fetch additional weather info if currentWeather is valid
                            val info = repo.getWeatherInfo(lat, lon, lang, units).collect { weatherIF ->
                                currentWeather.name = weatherIF.name
                                currentWeather.sunsetInfo = weatherIF.sys?.sunset
                                currentWeather.sunriseInfo = weatherIF.sys?.sunrise
                                insertCurrentWeatherToDb(currentWeather)
                            }
                        } else {
                            _currentWeather.value =
                                WeatherResult.Failure(Exception("No weather data found"))
                            Log.e("TAG", "Error: Current weather is null or incomplete")
                        }
                    }
            } else  {
                repo.getCurrentWeathers()
                    ?.catch { e ->
                        _currentWeather.value = WeatherResult.Failure(e)
                        Log.e("TAG", "Error fetching cached weather: ${e.localizedMessage}")
                    }
                    ?.collect { currentWeather ->
                        if (!currentWeather.isNullOrEmpty() && currentWeather[0]?.current != null) {
                            Log.i("TAG", "CurrentWeathers loaded: ${currentWeather[0]?.current}")
                            _currentWeather.value = WeatherResult.Success(currentWeather[0])
                        } else {
                            _currentWeather.value =
                                WeatherResult.Failure(Exception("No cached weather data found"))
                            Log.e("TAG", "Error: Cached weather is null or incomplete")
                        }
                    }

            }
        }
    }

    fun getWeatherInfo(lat: Double, lon: Double  ,lang: String,
                       units: String) {
        viewModelScope.launch {
            repo.getWeatherInfo(lat, lon,lang,
                units)
                .catch { e -> Log.i("TAG", "CurrentWeathers faild: ${e}") }
                .collect { currentWeather ->
                    Log.i("TAG", "CurrentWeathers loaded: ${currentWeather.name}")
                    _currentWeatherInfo.value = currentWeather
                }
        }
    }
   fun getCurrentWeathers()
    {
        viewModelScope.launch {
            repo.getCurrentWeathers()
                ?.catch { e ->
                    _currentWeather.value = WeatherResult.Failure(e)
                    Log.e("TAG", "Error fetching cached weather: ${e.localizedMessage}")
                }
                ?.collect { currentWeather ->
                    if (!currentWeather.isNullOrEmpty() && currentWeather[0]?.current != null) {
                        Log.i("TAG", "CurrentWeathers loaded: ${currentWeather[0]?.current}")
                        _currentWeather.value = WeatherResult.Success(currentWeather[0])
                    } else {
                        _currentWeather.value =
                            WeatherResult.Failure(Exception("No cached weather data found"))
                        Log.e("TAG", "Error: Cached weather is null or incomplete")
                    }
                }

        }
    }
    fun insertCurrentWeatherToDb(weather: WeatherResponse) {
        viewModelScope.launch {
            repo.insertOrUpdateCurrentWeather(weather)
        }
    }

    fun insertWeather(weather: WeatherResponse) {
        viewModelScope.launch {
            repo.insertWeather(weather)
        }
    }

    fun getFavoriteWeathers() {
        viewModelScope.launch {
            repo.getFavoriteWeathers()
                .catch { e -> Log.i("TAG", "FavoriteWeathers failed: ${e}") }
                .collect { favoriteWeatherList ->
                    if (favoriteWeatherList != null) {
                        Log.i("TAG", "FavoriteWeathers loaded: ${favoriteWeatherList.size}")
                        _favWeather.value = LocalDataState.Success(favoriteWeatherList)
                    } else {
                        Log.i("TAG", "FavoriteWeathers: No data found")
                        _favWeather.value =
                            LocalDataState.Success(emptyList()) // Return empty list if null
                    }
                }
        }
    }

    fun insertFavorite(lat: Double, lon: Double, lang: String, units: String) {
        val isOnline = NetworkUtils.isInternetAvailable(context)
        viewModelScope.launch {
            if (isOnline) {
                // Fetch current weather
                repo.getCurrentWeather(lat, lon,  lang, units, isOnline)
                    .catch { e ->
                        _favWeather.value = LocalDataState.Fail(e)
                    }
                    .collect { currentWeather ->
                        Log.i("TAG", "CurrentWeathers loaded: ${currentWeather.current}")
                        _favWeather.value = LocalDataState.Success(listOf(currentWeather))

                        // Fetch additional weather info (sunset, sunrise, etc.)
                        val weatherInfoFlow = repo.getWeatherInfo(lat, lon, lang,  units)

                        weatherInfoFlow
                            .catch { e ->
                                Log.e("TAG", "Error fetching weather info: $e")
                            }
                            .collect { weatherIF ->
                                currentWeather.name = weatherIF.name
                                currentWeather.sunsetInfo = weatherIF.sys?.sunset
                                currentWeather.sunriseInfo = weatherIF.sys?.sunrise
                                currentWeather.isFavorite = true

                                // Insert the updated current weather into the DB
                                insertCurrentWeatherToDb(currentWeather)
                            }
                    }
            } else {
                // Handle offline scenario: get data from local cache
                repo.getCurrentWeathers()
                    ?.catch { e ->
                        _currentWeather.value = WeatherResult.Failure(e)
                        Log.e("TAG", "Error fetching cached weather: ${e.localizedMessage}")
                    }
                    ?.collect { currentWeather ->
                        if (!currentWeather.isNullOrEmpty() && currentWeather[0]?.current != null) {
                            Log.i("TAG", "CurrentWeathers loaded: ${currentWeather[0]?.current}")
                            _currentWeather.value = WeatherResult.Success(currentWeather[0])
                        } else {
                            _currentWeather.value =
                                WeatherResult.Failure(Exception("No cached weather data found"))
                            Log.e("TAG", "Error: Cached weather is null or incomplete")
                        }
                    }
            }
        }
    }
    fun deleteFavorite(weather: WeatherResponse, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = repo.deleteFavorite(weather)
            onResult(result > 0)
        }
    }

}

class WeatherFactory(val repo: WeatherRepositry, val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(WeatherViewModel::class.java)) {
            WeatherViewModel(repo, context) as T
        } else {
            throw IllegalArgumentException("View Model Calss not found")
        }
    }
}