package com.example.skycast.model.pojo

import androidx.annotation.Keep
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather_info")
@Keep
data class WeatherInfo(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @Embedded val sys: Sys? = null,
    val name: String? = null
)

data class Sys(
    val country: String? = null,
    val sunrise: Int? = null,
    val sunset: Int? = null
)

