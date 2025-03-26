package com.example.skycast.model.remote

import com.example.skycast.model.models.WeatherInfo
import com.example.skycast.model.models.WeatherResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.http.Query

interface WeatherRemoteDataSource {
    fun getCurrentWeather(lat: Double,
                           lon: Double,
                         lang:String,
                         units: String = "metric") : Flow<WeatherResponse>
    fun getWeatherInfo(lat: Double,
                       lon: Double): Flow<WeatherInfo>
}