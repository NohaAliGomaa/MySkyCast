package com.example.skycast.model.util

import androidx.room.TypeConverter
import com.example.skycast.model.pojo.DailyItem
import com.example.skycast.model.pojo.HourlyItem
import com.example.skycast.model.pojo.MinutelyItem
import com.example.skycast.model.pojo.WeatherAlert
import com.example.skycast.model.pojo.WeatherItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class Converters {

    private val gson = Gson()

    @TypeConverter
    fun fromDailyItemList(value: List<DailyItem?>?): String? {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toDailyItemList(value: String?): List<DailyItem?>? {
        val listType = object : TypeToken<List<DailyItem?>>() {}.type
        return gson.fromJson(value, listType)
    }

    @TypeConverter
    fun fromHourlyItemList(value: List<HourlyItem?>?): String? {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toHourlyItemList(value: String?): List<HourlyItem?>? {
        val listType = object : TypeToken<List<HourlyItem?>>() {}.type
        return gson.fromJson(value, listType)
    }

    @TypeConverter
    fun fromMinutelyItemList(value: List<MinutelyItem?>?): String? {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toMinutelyItemList(value: String?): List<MinutelyItem?>? {
        val listType = object : TypeToken<List<MinutelyItem?>>() {}.type
        return gson.fromJson(value, listType)
    }

    @TypeConverter
    fun fromWeatherItemList(value: List<WeatherItem?>?): String? {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toWeatherItemList(value: String?): List<WeatherItem?>? {
        val listType = object : TypeToken<List<WeatherItem?>>() {}.type
        return gson.fromJson(value, listType)
    }
    @TypeConverter
    fun fromWeatherAlertList(alerts: List<WeatherAlert>?): String? {
        return gson.toJson(alerts)
    }

    @TypeConverter
    fun toWeatherAlertList(alertsJson: String?): List<WeatherAlert>? {
        return alertsJson?.let {
            val type = object : TypeToken<List<WeatherAlert>>() {}.type
            gson.fromJson(it, type)
        }
    }
}
