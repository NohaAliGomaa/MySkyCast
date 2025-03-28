package com.example.skycast.model.remote

import com.example.skycast.model.pojo.WeatherInfo
import com.example.skycast.model.pojo.WeatherResponse
import kotlinx.coroutines.flow.Flow

interface WeatherRemoteDataSource {
    fun getCurrentWeather(lat: Double,
                           lon: Double,
                         lang:String,
                         units: String = "metric") : Flow<WeatherResponse>
    fun getWeatherInfo(lat: Double,
                       lon: Double): Flow<WeatherInfo>
}