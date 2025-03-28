package com.example.skycast.model.local

import com.example.skycast.model.pojo.WeatherResponse
import kotlinx.coroutines.flow.Flow

interface ILocalDataSource {
    fun getFavoriteWeathers(): Flow<List<WeatherResponse>?>
    suspend fun insertWeather(weather: WeatherResponse): Long

    suspend fun deleteFavorite(weather: WeatherResponse): Int
    suspend fun deleteCurrent(): Int
    suspend fun insertCurrentWeather(weather: WeatherResponse): Long

    fun getCurrentWeathers(): Flow<WeatherResponse>?
    suspend fun insertOrUpdateCurrentWeather(weather: WeatherResponse)
    suspend fun updateFavWeather(weather: WeatherResponse)
}