package com.example.skycast.model.remote

import com.example.skycast.model.models.WeatherResponse
import kotlinx.coroutines.flow.Flow

interface WeatherRemoteDataSource {
    fun getCurrentWeather() : Flow<WeatherResponse>
}