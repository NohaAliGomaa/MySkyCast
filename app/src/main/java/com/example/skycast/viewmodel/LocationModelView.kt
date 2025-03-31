package com.example.skycast.viewmodel

import android.app.Application
import android.content.Context
import android.location.Geocoder
import android.location.LocationManager
import android.os.Looper
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.skycast.MyApp

import com.example.skycast.model.location.LocationState
import com.example.skycast.model.repositries.WeatherRepositry
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.*


class LocationViewModel(application: MyApp) : AndroidViewModel(application) {

    val geocoder = Geocoder(application.baseContext, Locale.getDefault())
    private val fusedLocationClient =
        LocationServices.getFusedLocationProviderClient(application)

    private val _locationState = MutableStateFlow(LocationState())
    val locationState: StateFlow<LocationState> = _locationState

    private val placesClient = Places.createClient(application.baseContext)
    val placeFields = listOf(Place.Field.LAT_LNG, Place.Field.ADDRESS, Place.Field.NAME)

    private val _selectedLocation = MutableStateFlow<LatLng?>(null)
    val selectedLocation: StateFlow<LatLng?> = _selectedLocation

   var query = mutableStateOf("")
    val queryFlow = MutableStateFlow("")
    var predictions = mutableStateOf<List<AutocompletePrediction>>(emptyList())

    init {
        viewModelScope.launch {
            queryFlow
                .debounce(300)
                .distinctUntilChanged()
                .collect { searchQuery ->
                    onQueryChanged(searchQuery)
                }
        }
    }

    fun setSelectedLocation(latLng: LatLng) {
        _selectedLocation.value = latLng
        predictions.value = emptyList()
        try {
            val addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            val address = addressList?.firstOrNull()
            query.value = address?.getAddressLine(0) ?: ""
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
    fun onQueryChanged(newQuery: String) {
        query.value = newQuery

        val request = FindAutocompletePredictionsRequest.builder()
            .setQuery(newQuery)
            .build()

        placesClient.findAutocompletePredictions(request)
            .addOnSuccessListener { response ->
                predictions.value = response.autocompletePredictions
            }
            .addOnFailureListener {
                predictions.value = emptyList()
            }
    }

    fun onPredictionSelected(prediction: AutocompletePrediction) {
        val placeId = prediction.placeId

        val request = FetchPlaceRequest.builder(placeId, listOf(Place.Field.LAT_LNG)).build()
        placesClient.fetchPlace(request)
            .addOnSuccessListener { response ->
                val place = response.place
                place.latLng?.let {
                    setSelectedLocation(it)
                }
                val latLng = response.place.latLng
                latLng?.let {
                    setSelectedLocation(it) // <--- This pins it on the map
                    getAddressFromLocation(it.latitude, it.longitude)
                    _locationState.value = _locationState.value.copy(
                        latitude = it.latitude,
                        longitude = it.longitude,
                        isLoading = false,
                        error = null
                    )

                }
                query.value = response.place.name?: "" // Set name in search bar
                predictions.value = emptyList()
            }
    }

    fun checkGpsEnabled() {
        val locationManager = getApplication<Application>()
            .getSystemService(Context.LOCATION_SERVICE) as LocationManager
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
class LocationFactory(val app : MyApp): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if(modelClass.isAssignableFrom(LocationViewModel::class.java)){
            LocationViewModel(app) as T
        }
        else{
            throw IllegalArgumentException("View Model Calss not found")
        }
    }
}
