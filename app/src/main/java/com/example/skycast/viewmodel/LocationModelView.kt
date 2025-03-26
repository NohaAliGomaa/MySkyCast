package com.example.skycast.viewmodel

import android.app.Application
import android.content.Context
import android.location.Geocoder
import android.location.LocationManager
import android.os.Looper
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope

import com.example.skycast.model.location.LocationState
import com.google.android.gms.location.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.*

class LocationViewModel(application: Application) : AndroidViewModel(application) {

    private val fusedLocationClient =
        LocationServices.getFusedLocationProviderClient(application)

    private val _locationState = MutableStateFlow(LocationState())
    val locationState: StateFlow<LocationState> = _locationState

    fun checkGpsEnabled() {
        val locationManager = getApplication<Application>().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        _locationState.value = _locationState.value.copy(
            isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        )
    }

    fun setPermissionError(message: String) {
        _locationState.value = _locationState.value.copy(
            isLoading = false,
            error = message
        )
    }

    fun fetchLocation() {
        viewModelScope.launch {
            _locationState.value = _locationState.value.copy(isLoading = true, error = null)

            val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000L)
                .setWaitForAccurateLocation(true)
                .setMinUpdateIntervalMillis(2000L)
                .build()

            try {
                fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    object : LocationCallback() {
                        override fun onLocationResult(result: LocationResult) {
                            val location = result.lastLocation ?: return

                            val lat = location.latitude
                            val lon = location.longitude

                            getAddressFromLocation(lat, lon)

                            _locationState.value = _locationState.value.copy(
                                latitude = lat,
                                longitude = lon,
                                isLoading = false,
                                error = null
                            )

                            fusedLocationClient.removeLocationUpdates(this)
                        }
                    },
                    Looper.getMainLooper()
                )
            } catch (e: SecurityException) {
                _locationState.value = _locationState.value.copy(
                    error = "Location permission not granted",
                    isLoading = false
                )
            } catch (e: Exception) {
                _locationState.value = _locationState.value.copy(
                    error = "Error getting location: ${e.message}",
                    isLoading = false
                )
            }
        }
    }

    private fun getAddressFromLocation(lat: Double, lon: Double) {
        viewModelScope.launch {
            try {
                val geocoder = Geocoder(getApplication())
                val addresses = geocoder.getFromLocation(lat, lon, 1)

                if (!addresses.isNullOrEmpty()) {
                    val address = addresses[0]
                    val city = address.adminArea?: "Unknown city"
                    val fullAddress = buildString {
                        append(address.getAddressLine(0) ?: "")
                        append("\n")
                        append(city)
                        append(", ")
                        append(address.adminArea ?: "")
                        append(" ")
                        append(address.postalCode ?: "")
                    }

                    _locationState.value = _locationState.value.copy(
                        address = fullAddress,
                        cityName = city
                    )
                } else {
                    _locationState.value = _locationState.value.copy(
                        address = "No address found",
                        cityName = "Unknown city"
                    )
                }
            } catch (e: IOException) {
                Log.e("Geocoder", "Failed to get address: ${e.message}")
                _locationState.value = _locationState.value.copy(
                    address = "Failed to get address",
                    cityName = "Unknown city"
                )
            }
        }
    }

}
