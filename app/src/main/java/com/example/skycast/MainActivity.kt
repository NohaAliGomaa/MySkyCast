package com.example.skycast

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import com.example.skycast.ui.theme.SkyCastTheme
import com.example.skycast.home.WeatherScreen
import com.example.skycast.model.remote.WeatherRemoteDataSourceImpl
import com.example.skycast.model.repositries.WeatherRepositry
import com.example.skycast.model.result.WeatherResult
import com.example.skycast.model.route.ScreenRout
import com.example.skycast.model.util.AppConstants
import com.example.skycast.splash.SplashScreen
import com.example.skycast.viewmodel.LocationViewModel
import com.example.skycast.viewmodel.WeatherFactory
import com.example.skycast.viewmodel.WeatherViewModel

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val factory = WeatherFactory(WeatherRepositry(WeatherRemoteDataSourceImpl()))
        val viewModel = ViewModelProvider(this, factory).get(WeatherViewModel::class.java)

        setContent {
            SkyCastTheme {
                AppNavigation(viewModel)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation(
    viewModel: WeatherViewModel,
    locationViewModel: LocationViewModel = viewModel()
) {
    val context = LocalContext.current
    val locationState by locationViewModel.locationState.collectAsState()
    val weather by viewModel.weather.collectAsStateWithLifecycle()
    val navController = rememberNavController()

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            locationViewModel.fetchLocation()
        } else {
            locationViewModel.setPermissionError("Location permission denied.")
        }
    }

    NavHost(
        navController = navController,
        startDestination = ScreenRout.SplashScreenRoute.route,
        modifier = Modifier.fillMaxSize()
    ) {
        composable(ScreenRout.SplashScreenRoute.route) {
            SplashScreen {
                navController.navigate(ScreenRout.HomeScreenRoute.route) {
                    popUpTo(ScreenRout.SplashScreenRoute.route) { inclusive = true }
                }
            }
        }

        composable(ScreenRout.HomeScreenRoute.route) {
            // Permission + GPS Handling
            LaunchedEffect(locationState.isGpsEnabled) {
                locationViewModel.checkGpsEnabled()
                if (locationState.isGpsEnabled) {
                    val fineGranted = ContextCompat.checkSelfPermission(
                        context, Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                    val coarseGranted = ContextCompat.checkSelfPermission(
                        context, Manifest.permission.ACCESS_COARSE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED

                    if (!fineGranted && !coarseGranted) {
                        permissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    } else {
                        locationViewModel.fetchLocation()
                    }
                }
            }

            // UI
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (!locationState.isGpsEnabled) {
                    Text("GPS is disabled. Please enable it in settings.")
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(onClick = {
                        context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                    }) {
                        Text("Open Location Settings")
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                if (locationState.isLoading) {
                    CircularProgressIndicator()
                }

                locationState.error?.let {
                    Text(
                        it,
                        color = Color.Red,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }

            // Fetch Weather
            LaunchedEffect(locationState.latitude, locationState.longitude) {
                if (locationState.latitude != null && locationState.longitude != null) {
                    viewModel.getCurrentWeather(
                        lat = locationState.latitude!!,
                        lon = locationState.longitude!!,
                        units = AppConstants.WEATHER_UNIT
                    )
                }
            }

            // Display Weather
            when (weather) {
                is WeatherResult.Loading -> {
                    CircularProgressIndicator()
                }

                is WeatherResult.Success -> {
                    val currentWeather = (weather as WeatherResult.Success).data
                    WeatherScreen(currentWeather, locationState.cityName ?: "Unknown")
                }

                is WeatherResult.Failure -> {
                    val errorMessage =
                        (weather as WeatherResult.Failure).error.message ?: "Unknown Error"
                    Log.e("AppNavigation", "Weather Error: $errorMessage")
                }
            }
        }
    }
}

// Optional - Keep this for future reference
//@Composable
//fun LocationScreen(viewModel: LocationViewModel = viewModel()) { ... }
