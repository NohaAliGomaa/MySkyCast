package com.example.skycast.model.remote

import com.example.skycast.model.models.WeatherResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.http.Query

interface WeatherRemoteDataSource {
    fun getCurrentWeather(lat: Double,
                           lon: Double,
                         units: String = "metric") : Flow<WeatherResponse>
}