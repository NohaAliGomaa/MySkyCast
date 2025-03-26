package com.example.skycast.model.repositries

import com.example.skycast.model.models.WeatherInfo
import com.example.skycast.model.models.WeatherResponse
import kotlinx.coroutines.flow.Flow

interface WeatherRepositryInterface {
    fun getCurrentWeather(lat: Double,
                          lon: Double,
                          lang:String,
                          units: String) : Flow<WeatherResponse>
    fun getWeatherInfo(lat: Double, lon: Double): Flow<WeatherInfo>
}