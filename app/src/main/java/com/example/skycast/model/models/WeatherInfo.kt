package com.example.skycast.model.models

data class WeatherInfo(
	val visibility: Int? = null,
	val timezone: Int? = null,
	val main: Main? = null,
	val clouds: Clouds? = null,
	val sys: Sys? = null,
	val dt: Int? = null,
	val coord: Coord? = null,
	val weather: List<WeatherItem?>? = null,
	val name: String? = null,
	val cod: Int? = null,
	val id: Int? = null,
	val base: String? = null,
	val wind: Wind? = null
)

data class Main(
	val temp: Double? = null,
	val tempMin:Double? = null,
	val grndLevel: Double? = null,
	val humidity: Double? = null,
	val pressure: Double? = null,
	val seaLevel: Double? = null,
	val feelsLike: Any? = null,
	val tempMax: Double? = null
)

data class Sys(
	val country: String? = null,
	val sunrise: Int? = null,
	val sunset: Int? = null
)

data class Coord(
	val lon: Any? = null,
	val lat: Any? = null
)

data class Clouds(
	val all: Int? = null
)

data class Wind(
	val deg: Int? = null,
	val speed: Any? = null,
	val gust: Any? = null
)

