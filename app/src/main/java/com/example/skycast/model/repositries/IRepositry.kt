package com.example.skycast.model.repositries

import com.example.skycast.model.pojo.Settings
import com.example.skycast.model.pojo.WeatherInfo
import com.example.skycast.model.pojo.WeatherResponse
import kotlinx.coroutines.flow.Flow

interface IRepositry {
    suspend  fun getCurrentWeather(lat: Double,
                          lon: Double,
                          lang:String,
                          units: String, isOnline : Boolean ) : Flow<WeatherResponse>
    suspend fun getWeatherInfo(lat: Double, lon: Double, lang:String,
                               units: String): Flow<WeatherInfo>
    suspend  fun getFavoriteWeathers(): Flow<List<WeatherResponse>?>
    fun getCurrentWeathers():Flow<List<WeatherResponse>?>
    suspend fun insertWeather(weather: WeatherResponse): Long
    suspend fun insertCurrentWeather(weather: WeatherResponse): Long
    suspend fun insertOrUpdateCurrentWeather(weather: WeatherResponse)
     suspend fun deleteFavorite(weather: WeatherResponse): Int
    fun saveSettings(settings: Settings)
    fun getSettings():Settings?
//    fun saveAlertSettings(alertSettings: AlertSettings)
//    fun getAlertSettings():AlertSettings?
}