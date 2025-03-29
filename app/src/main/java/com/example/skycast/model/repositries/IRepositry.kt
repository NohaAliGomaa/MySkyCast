package com.example.skycast.model.repositries

import com.example.skycast.model.pojo.WeatherInfo
import com.example.skycast.model.pojo.WeatherResponse
import kotlinx.coroutines.flow.Flow

interface IRepositry {
    suspend  fun getCurrentWeather(lat: Double,
                          lon: Double,
                          lang:String,
                          units: String, isOnline : Boolean ) : Flow<WeatherResponse>
    suspend fun getWeatherInfo(lat: Double, lon: Double): Flow<WeatherInfo>
    suspend  fun getFavoriteWeathers(): Flow<List<WeatherResponse>?>
    suspend fun getCurrentWeathers(): Flow<WeatherResponse>?
    suspend fun insertWeather(weather: WeatherResponse): Long
    suspend fun insertCurrentWeather(weather: WeatherResponse): Long
}