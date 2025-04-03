package com.example.skycast.setting

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.benchmark.json.BenchmarkData
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.skycast.model.local.LocalDataSource
import com.example.skycast.model.pojo.Settings
import com.example.skycast.model.pojo.WeatherResponse
import com.example.skycast.model.remote.RemoteDataSourceImpl
import com.example.skycast.model.repositries.WeatherRepositry
import com.example.skycast.model.sharedpreferences.SharedManager
import com.example.skycast.model.util.AppConstants
import com.example.skycast.ui.theme.PrimaryColor
import com.example.skycast.ui.theme.TertiaryColor
import com.example.skycast.viewmodel.LocationViewModel
import com.example.skycast.viewmodel.SettingsViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


@Composable
fun SettingsScreen(onNavToHome:()->Unit, settingsViewModel: SettingsViewModel = viewModel(),
                   viewModel: LocationViewModel
) {
    val settings by settingsViewModel.settings.collectAsState(initial = Settings())

    val context = LocalContext.current
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(TertiaryColor.value),
                        Color(PrimaryColor.value)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ){
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Settings", style = MaterialTheme.typography.headlineMedium)

            // Location Selection (GPS or Map)
            LocationSelection(settings,{ isMapSelected ->
                settingsViewModel.saveSettings(settings.copy(isMap = isMapSelected))
            }, viewModel,settingsViewModel)

            // Temperature Unit Selection
            TemperatureUnitSelection(settings) { selectedUnit ->
                settingsViewModel.saveSettings(settings.copy(unit = selectedUnit))
            }

            // Language Selection
            LanguageSelection(settings) { selectedLanguage ->
                settingsViewModel.saveSettings(settings.copy(lang = selectedLanguage))
            }
            // Save Button
            Button(onClick = {
                settingsViewModel.saveSettings(settings)
                onNavToHome()}) {
                Text(text = "Save & Back")
            }
        }
    }
}

@Composable
fun LocationSelection(
    settings: Settings,
    onSelectionChange: (Boolean) -> Unit,
    viewModel: LocationViewModel,
    settingsViewModel: SettingsViewModel
) {
    Column {
        Text(text = "Location Source", style = MaterialTheme.typography.titleMedium)

        listOf("GPS" to false, "Map" to true).forEach { (label, value) ->
            RadioButtonWithLabel(
                text = label,
                selected = settings.isMap == value,
                onSelected = { onSelectionChange(value) }
            )
        }
        // Show Map if Map is selected
        if (settings.isMap) {
            MapViewComposable(viewModel , onSelectionChange,settings,settingsViewModel)
        }
    }
}

@Composable
fun MapViewComposable(
    viewModel: LocationViewModel,
    onSelectionChange: (Boolean) -> Unit,
    settings: Settings,
    settingsViewModel: SettingsViewModel
) {
    val context = LocalContext.current
    val mapView = remember { MapView(context) }
    val isMapReady = remember { mutableStateOf(false) }
    var selectedLatLng by remember { mutableStateOf<LatLng?>(null) }

    // Ensure lifecycle methods are properly handled
    DisposableEffect(Unit) {
        mapView.onCreate(Bundle())
        mapView.onStart()
        mapView.onResume()

        mapView.getMapAsync { googleMap ->
            isMapReady.value = true
            googleMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(30.593819, 32.269947), 12f
                )
            )

            googleMap.setOnMapClickListener { latLng ->
                // Check if selected LatLng is valid
                if (isValidLatLng(latLng.latitude, latLng.longitude)) {
                    selectedLatLng = latLng
                    val updatedSettings = settings.copy(lat = latLng.latitude, lon = latLng.longitude)
                    settingsViewModel.saveSettings(updatedSettings) // Save updated settings
                    googleMap.clear()
                    googleMap.addMarker(
                        MarkerOptions().position(latLng).title("Selected Location")
                    )
                    Log.d("MapView", "Selected location: $latLng")
                } else {
                    Toast.makeText(context, "Invalid location selected!", Toast.LENGTH_SHORT).show()
                }
            }
        }
        onDispose {
            mapView.onPause()
            mapView.onStop()
            mapView.onDestroy()
        }
    }

    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
        if (isMapReady.value) {
            AndroidView(
                factory = { mapView },
                modifier = Modifier.weight(1f)
            )
            // Show the button only if selectedLatLng is not null
            selectedLatLng?.let {
                Button(
                    onClick = {
                        settings.lat = it.latitude
                        settings.lon = it.longitude
                        viewModel.setSelectedLocation(it)
                        settingsViewModel.saveSettings(settings)
                        onSelectionChange(false) // Hide the map after selecting
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(text = "Confirm Location")
                }
            } ?: run {
                // Log if selectedLatLng is null
                Log.d("MapView", "No location selected yet")
            }
        }
    }
}


fun isValidLatLng(latitude: Double, longitude: Double): Boolean {
    return latitude in -90.0..90.0 && longitude in -180.0..180.0
}
@Composable
fun TemperatureUnitSelection(settings: Settings, onSelectionChange: (String) -> Unit) {
    Column {
        Text(text = "Temperature Unit", style = MaterialTheme.typography.titleMedium)

        listOf(
            "Kelvin" to AppConstants.UNITS_DEFAULT,
            "Fahrenheit" to AppConstants.UNITS_FAHRENHEIT,
            "Celsius" to AppConstants.UNITS_CELSIUS
        ).forEach { (label, value) ->
            RadioButtonWithLabel(
                text = label,
                selected = settings.unit == value,
                onSelected = { onSelectionChange(value) }
            )
        }
    }
}

@Composable
fun LanguageSelection(settings: Settings, onSelectionChange: (String) -> Unit) {
    Column {
        Text(text = "Language", style = MaterialTheme.typography.titleMedium)

        listOf("English" to AppConstants.LANG_EN, "Arabic" to AppConstants.LANG_AR).forEach { (label, value) ->
            RadioButtonWithLabel(
                text = label,
                selected = settings.lang == value,
                onSelected = { onSelectionChange(value) }
            )
        }
    }
}

@Composable
fun RadioButtonWithLabel(text: String, selected: Boolean, onSelected: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(selected = selected, onClick = onSelected)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = selected, onClick = onSelected)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text)
    }
}

//@RequiresApi(Build.VERSION_CODES.O)
//@Preview(showSystemUi = true)
//@Composable
//fun SettingsScreenPreview() {
//    SettingsScreen()
//}
