package com.example.skycast.model.pojo

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.TypeConverters
import com.example.skycast.model.util.Converters
import kotlinx.android.parcel.Parcelize

@Parcelize
@TypeConverters(Converters::class)
data class WeatherAlert(
    val sender_name: String,
    val event: String,
    val start: Long,
    val end: Long,
    val description: String,
    val tags: List<String>
): Parcelable