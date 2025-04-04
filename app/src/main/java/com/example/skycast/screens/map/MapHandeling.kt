package com.example.skycast.screens.map

import android.os.Bundle
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.skycast.model.sharedpreferences.SharedManager
import com.example.skycast.model.util.AppConstants
import com.example.skycast.ui.theme.TertiaryColor
import com.example.skycast.viewmodel.LocationViewModel
import com.example.skycast.viewmodel.WeatherViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState

@Composable
fun LocationScreen(locationViewModel: LocationViewModel ,
                   viewModel: WeatherViewModel,
                   onNavigateToFav : ()->Unit)  {
    viewModel.getFavoriteWeathers()
    val selectedLatLng by locationViewModel.selectedLocation.collectAsState()

    Column {
        SearchBar(locationViewModel)
        Spacer(modifier = Modifier.height(8.dp))
        locationViewModel.checkGpsEnabled()
        locationViewModel.fetchLocation()
        LocationPickerMap(
            selectedLatLng = selectedLatLng,
            onLocationSelected = { locationViewModel.setSelectedLocation(it)},
            viewModel,{ onNavigateToFav() }
        )

    }
}
@Composable
fun LocationPickerMap(
    selectedLatLng: LatLng?,
    onLocationSelected: (LatLng) -> Unit,
    viewModel: WeatherViewModel,
    onNavigateToFav: () -> Unit
) {
    val defaultLocation = LatLng(30.593819, 32.269947)
    val markerState = rememberMarkerState(position = selectedLatLng ?: defaultLocation)
    val cameraPositionState = rememberCameraPositionState{
        position = CameraPosition.fromLatLngZoom(markerState.position, 10f)
    }

    // New state for showing location selection dialog
    var isLocationDialogVisible by remember { mutableStateOf(false) }

    // Always move camera and marker when location changes
    LaunchedEffect(selectedLatLng) {
        selectedLatLng?.let {
            markerState.position = it
            cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(it, 10f))
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(isMyLocationEnabled = true),
            uiSettings = MapUiSettings(scrollGesturesEnabled = true, zoomGesturesEnabled = true),
            onMapClick = { latLng ->
                onLocationSelected(latLng)
                // Show dialog after selecting a location
                isLocationDialogVisible = true
            }
        ) {
            Marker(
                state = markerState,
                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED),
                title = "Selected Location"
            )
        }

        // Add the button on top of the map
        Button(
            onClick = {
                viewModel.insertFavorite(
                    selectedLatLng?.latitude ?: defaultLocation.latitude,
                    selectedLatLng?.longitude ?: defaultLocation.longitude,
                    SharedManager.getSettings()?.lang ?: AppConstants.LANG_EN,
                    SharedManager.getSettings()?.unit ?: AppConstants.WEATHER_UNIT
                )
                viewModel.getFavoriteWeathers()
                onNavigateToFav()
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) {
            Text("Add To Favourite")
        }

        // Show dialog to choose between selecting a new location or picking an existing one
        if (isLocationDialogVisible) {
            LocationSelectionDialog(
                onSelectExisting = {
                    // Navigate to the list of favorites
                    onNavigateToFav()
                    viewModel.getFavoriteWeathers()
                    isLocationDialogVisible = false
                },
                onSelectNew = {
                    // Add the location as new favorite and dismiss the dialog
                    viewModel.insertFavorite(
                        selectedLatLng?.latitude ?: defaultLocation.latitude,
                        selectedLatLng?.longitude ?: defaultLocation.longitude,
                        SharedManager.getSettings()?.lang ?: AppConstants.LANG_EN,
                        SharedManager.getSettings()?.unit ?: AppConstants.WEATHER_UNIT
                    )
                    viewModel.getFavoriteWeathers()
                    isLocationDialogVisible = false
                }
            )
        }
    }
}

@Composable
fun LocationSelectionDialog(
    onSelectExisting: () -> Unit,
    onSelectNew: () -> Unit
) {
    // Dialog content where user chooses between existing or new location
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp)
    ) {
        Text("Choose an Option", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { onSelectExisting() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Select from Existing Locations")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { onSelectNew() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Use This New Location")
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(viewModel: LocationViewModel) {
    val query = viewModel.query.value
    val predictions = viewModel.predictions.value

    OutlinedTextField(
        value = query,
        onValueChange = { viewModel.onQueryChanged(it)},
        placeholder = {
            Text("Search location", color = Color.White)
        },
        leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = null)
        },
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(TertiaryColor.value)),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            disabledTextColor = Color.White,
            disabledPlaceholderColor = Color.White.copy(alpha = 0.6f),
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,
            cursorColor = Color.White,
            disabledLeadingIconColor = Color.White
        )
    )
    LazyColumn {
        items(predictions) { prediction ->
            Text(
                text = prediction.getFullText(null).toString(),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        viewModel.onPredictionSelected(prediction)
                    }
                    .padding(8.dp)
            )
        }
    }
}
