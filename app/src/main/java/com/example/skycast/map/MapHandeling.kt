package com.example.skycast.map

import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.skycast.R
import com.example.skycast.ui.theme.TertiaryColor
import com.example.skycast.viewmodel.LocationViewModel
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState

@Composable
fun LocationScreen(viewModel: LocationViewModel) {
    val selectedLatLng by viewModel.selectedLocation.collectAsState()

    Column {
        SearchBar(viewModel)
        Spacer(modifier = Modifier.height(8.dp))
        viewModel.checkGpsEnabled()
        viewModel.fetchLocation()
        LocationPickerMap(
            selectedLatLng = selectedLatLng,
            onLocationSelected = { viewModel.setSelectedLocation(it) }
        )
    }
}

@Composable
fun LocationPickerMap(
    selectedLatLng: LatLng?,
    onLocationSelected: (LatLng) -> Unit
) {
    val defaultLocation = LatLng(30.593819, 32.269947)
    val cameraPositionState = rememberCameraPositionState()
    val markerState = rememberMarkerState(position = selectedLatLng ?: defaultLocation)

    // Always move camera and marker when location changes
    LaunchedEffect(selectedLatLng) {
        selectedLatLng?.let {
            markerState.position = it
            cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(it, 10f))
        }
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        onMapClick = { latLng ->
            onLocationSelected(latLng) // this should call viewModel.setSelectedLocation()
        }
    ) {
        Marker(
            state = markerState,
            title = "Selected Location"
        )
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
