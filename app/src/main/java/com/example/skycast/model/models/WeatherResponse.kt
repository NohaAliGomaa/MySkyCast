package com.example.skycast.model.models

import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    val current: Current? = null,
    val timezone: String? = null,
    val timezoneOffset: Int? = null,
    val daily: List<DailyItem?>? = null,
    val lon: Any? = null,
    val hourly: List<HourlyItem?>? = null,
    val minutely: List<MinutelyItem?>? = null,
    val lat: Any? = null
)

data class HourlyItem(
    val temp: Any? = null,
    val visibility: Int? = null,
    val uvi: Any? = null,
    val pressure: Int? = null,
    val clouds: Int? = null,
    val feelsLike: Any? = null,
    val windGust: Any? = null,
    val dt: Int? = null,
    val pop: Int? = null,
    val windDeg: Int? = null,
    val dewPoint: Any? = null,
    val weather: List<WeatherItem?>? = null,
    val humidity: Int? = null,
    val windSpeed: Any? = null,
    val rain: Rain? = null
)

data class DailyItem(
    val moonset: Int? = null,
    val summary: String? = null,
    val rain: Any? = null,
    val sunrise: Int? = null,
    val temp: Temp? = null,
    val moonPhase: Any? = null,
    val uvi: Any? = null,
    val moonrise: Int? = null,
    val pressure: Int? = null,
    val clouds: Int? = null,
    val feelsLike: FeelsLike? = null,
    val windGust: Any? = null,
    val dt: Int? = null,
    val pop: Int? = null,
    val windDeg: Int? = null,
    val dewPoint: Any? = null,
    val sunset: Int? = null,
    val weather: List<WeatherItem?>? = null,
    val humidity: Int? = null,
    val windSpeed: Any? = null
)

data class Current(
    val sunrise: Int? = null,
    val temp: Any? = null,
    val visibility: Int? = null,
    val uvi: Any? = null,
    val pressure: Int? = null,
    val clouds: Int? = null,
    val feelsLike: Any? = null,
    @SerializedName("wind_gust") val windGust: Any? = null,
    val dt: Int? = null,
    @SerializedName("wind_deg") val windDeg: Int? = null,
    val dewPoint: Any? = null,
    val sunset: Int? = null,
    val weather: List<WeatherItem?>? = null,
    val humidity: Int? = null,
    @SerializedName("wind_speed") val windSpeed: Any? = null
)

data class FeelsLike(
    val eve: Any? = null,
    val night: Any? = null,
    val day: Any? = null,
    val morn: Any? = null
)

data class Temp(
    val min: Any? = null,
    val max: Any? = null,
    val eve: Any? = null,
    val night: Any? = null,
    val day: Any? = null,
    val morn: Any? = null
)

data class WeatherItem(
    val icon: String? = null,
    val description: String? = null,
    val main: String? = null,
    val id: Int? = null
)

data class Rain(
    val jsonMember1h: Any? = null
)

data class MinutelyItem(
    val dt: Int? = null,
    val precipitation: Int? = null
)

