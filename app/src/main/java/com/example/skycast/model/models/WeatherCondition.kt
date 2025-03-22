package com.example.skycast.model.models

data class WeatherCondition(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
)