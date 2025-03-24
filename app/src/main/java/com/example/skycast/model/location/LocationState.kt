package com.example.skycast.model.location

import android.location.Location

data class LocationState(
    val latitude: Double? = null,
    val longitude: Double? = null,
    val address: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isGpsEnabled: Boolean = true
)
