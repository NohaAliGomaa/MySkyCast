package com.example.skycast.model.result

import com.example.skycast.model.pojo.WeatherInfo
import com.example.skycast.model.pojo.WeatherResponse
sealed class WeatherResult {
    data object Loading : WeatherResult()
    data class Success(val data: WeatherResponse) : WeatherResult()
    data class Failure(val error: Throwable) : WeatherResult()
}
sealed class WeatherInfoResult {
    object Loading : WeatherInfoResult()
    data class Success(val weatherInfo: WeatherInfo) : WeatherInfoResult()
    data class Failure(val throwable: Throwable) : WeatherInfoResult()
}
