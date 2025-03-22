package com.example.skycast.model.models

data class WeatherResponse(
    val lat: Double,
    val lon: Double,
    val timezone: String,
    val timezone_offset: Int,
    val current: CurrentWeather,
    val minutely: List<MinutelyWeather>,
    val hourly: List<HourlyWeather>,
    val daily: List<DailyWeather>,
    val alerts: List<WeatherAlert>?
)