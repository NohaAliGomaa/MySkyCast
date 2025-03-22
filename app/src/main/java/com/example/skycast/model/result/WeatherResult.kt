package com.example.skycast.model.result

import com.example.skycast.model.models.WeatherResponse
sealed class WeatherResult {
    data object Loading : WeatherResult()
    data class Success(val data: WeatherResponse) : WeatherResult()
    data class Failure(val error: Throwable) : WeatherResult()
}