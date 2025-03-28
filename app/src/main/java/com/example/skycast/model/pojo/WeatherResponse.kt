package com.example.skycast.model.pojo

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.skycast.model.local.Converters
import com.google.gson.annotations.SerializedName

@Entity(tableName = "weather")
data class WeatherResponse(
    val lat: Any? = null,
    val lon: Any? = null,
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @Embedded val current: Current? = null,
    val timezone: String? = null,
    val timezoneOffset: Int? = null,
    @TypeConverters(Converters::class) val daily: List<DailyItem?>? = null,
    @TypeConverters(Converters::class) val hourly: List<HourlyItem?>? = null,
    @TypeConverters(Converters::class) val minutely: List<MinutelyItem?>? = null,
    var isFavorite:Boolean? = false
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
    @TypeConverters(Converters::class) val weather: List<WeatherItem?>? = null,
    val humidity: Int? = null,
    val windSpeed: Any? = null,
    @Embedded val rain: Rain? = null
)

data class DailyItem(
    val moonset: Int? = null,
    val summary: String? = null,
    val rain: Any? = null,
    val sunrise: Int? = null,
    @Embedded(prefix = "temp_")val temp: Temp? = null,
    val moonPhase: Any? = null,
    val uvi: Any? = null,
    val moonrise: Int? = null,
    val pressure: Int? = null,
    val clouds: Int? = null,
    @Embedded(prefix = "feelsLike_")val feelsLike: FeelsLike? = null,
    val windGust: Any? = null,
    val dt: Int? = null,
    val pop: Int? = null,
    val windDeg: Int? = null,
    val dewPoint: Any? = null,
    val sunset: Int? = null,
    @TypeConverters(Converters::class) val weather: List<WeatherItem?>? = null,
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
    @SerializedName("feels_like")val feelsLike: Double? = null,
    @SerializedName("wind_gust") val windGust: Any? = null,
    val dt: Int? = null,
    @SerializedName("wind_deg") val windDeg: Int? = null,
    val dewPoint: Any? = null,
    val sunset: Int? = null,
    @TypeConverters(Converters::class) val weather: List<WeatherItem?>? = null,
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
    @ColumnInfo(name = "1h") val jsonMember1h: Any? = null
)

data class MinutelyItem(
    val dt: Int? = null,
    val precipitation: Int? = null
)

