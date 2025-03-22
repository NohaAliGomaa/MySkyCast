package com.example.skycast.model.models

data class WeatherSummaryResponse(
    val lat: Double,
    val lon: Double,
    val tz: String,
    val date: String,
    val units: String,
    val weather_overview: String
)