package com.example.skycast.model.pojo

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.skycast.model.local.Converters
import com.google.gson.annotations.SerializedName

@Keep
@Entity(tableName = "weather")
@TypeConverters(Converters::class)
data class WeatherResponse(
    val lat: Double? = null,
    val lon: Double? = null,
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @Embedded val current: Current? = null,
    val timezone: String? = null,
    val timezoneOffset: Int? = null,
    @TypeConverters(Converters::class) val daily: List<DailyItem?>? = null,
    @TypeConverters(Converters::class) val hourly: List<HourlyItem?>? = null,
    @TypeConverters(Converters::class) val minutely: List<MinutelyItem?>? = null,
    var isFavorite:Boolean? = false,
    var name: String? = null,
    var sunriseInfo: Int? = null,
    var sunsetInfo: Int? = null
)

data class HourlyItem(
    val temp: Double? = null,
    val visibility: Int? = null,
    val uvi: Double? = null,
    val pressure: Int? = null,
    val clouds: Int? = null,
    val feelsLike: Double? = null,
    val windGust: Double? = null,
    val dt: Int? = null,
    val pop: Int? = null,
    val windDeg: Int? = null,
    val dewPoint: Double? = null,
    @TypeConverters(Converters::class) val weather: List<WeatherItem?>? = null,
    val humidity: Int? = null,
    val windSpeed: Double? = null,
    @Embedded val rain: Rain? = null
)

data class DailyItem(
    val moonset: Int? = null,
    val summary: String? = null,
    val rain: Double? = null,
    val sunrise: Int? = null,
    @Embedded(prefix = "temp_")val temp: Temp? = null,
    val moonPhase: Double? = null,
    val uvi: Double? = null,
    val moonrise: Int? = null,
    val pressure: Int? = null,
    val clouds: Int? = null,
    @Embedded(prefix = "feelsLike_")val feelsLike: FeelsLike? = null,
    val windGust: Double? = null,
    val dt: Int? = null,
    val pop: Int? = null,
    val windDeg: Int? = null,
    val dewPoint: Double? = null,
    val sunset: Int? = null,
    @TypeConverters(Converters::class) val weather: List<WeatherItem?>? = null,
    val humidity: Int? = null,
    val windSpeed: Double? = null
)

data class Current(
    val sunrise: Int? = null,
    val temp: Double? = null,
    val visibility: Int? = null,
    val uvi: Double? = null,
    val pressure: Int? = null,
    val clouds: Int? = null,
    @SerializedName("feels_like")val feelsLike: Double? = null,
    @SerializedName("wind_gust") val windGust: Double? = null,
    val dt: Int? = null,
    @SerializedName("wind_deg") val windDeg: Int? = null,
    val dewPoint:Double? = null,
    val sunset: Int? = null,
    @TypeConverters(Converters::class) val weather: List<WeatherItem?>? = null,
    val humidity: Int? = null,
    @SerializedName("wind_speed") val windSpeed:Double? = null
)

data class FeelsLike(
    val eve: Double? = null,
    val night: Double? = null,
    val day: Double? = null,
    val morn: Double? = null
)

data class Temp(
    val min: Double? = null,
    val max: Double? = null,
    val eve: Double? = null,
    val night: Double? = null,
    val day: Double? = null,
    val morn: Double? = null
)

data class WeatherItem(
    val icon: String? = null,
    val description: String? = null,
    val main: String? = null,
    val id: Int? = null
)

data class Rain(
    @ColumnInfo(name = "1h") val jsonMember1h: Double? = null
)

data class MinutelyItem(
    val dt: Int? = null,
    val precipitation: Int? = null
)

