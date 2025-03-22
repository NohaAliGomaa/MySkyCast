package com.example.skycast.model.models

data class WeatherAlert(
    val sender_name: String,
    val event: String,
    val start: Long,
    val end: Long,
    val description: String,
    val tags: List<String>
)