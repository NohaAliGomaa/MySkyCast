package com.example.skycast.model.pojo


data class WeatherInfo(
    val id: Int = 0,
   val sys: Sys? = null,
    val name: String? = null
)

data class Sys(
    val country: String? = null,
    val sunrise: Int? = null,
    val sunset: Int? = null
)

