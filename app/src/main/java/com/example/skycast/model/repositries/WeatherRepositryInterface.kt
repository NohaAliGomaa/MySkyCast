package com.example.skycast.model.repositries

import com.example.skycast.model.models.WeatherResponse
import kotlinx.coroutines.flow.Flow

interface WeatherRepositryInterface {
    fun getCurrentWeather() : Flow<WeatherResponse>
}