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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.*
import com.example.skycast.ui.theme.SkyCastTheme
import com.example.skycast.home.WeatherScreen
import com.example.skycast.model.remote.WeatherRemoteDataSourceImpl
import com.example.skycast.model.repositries.WeatherRepositry
import com.example.skycast.model.result.WeatherInfoResult
import com.example.skycast.model.result.WeatherResult
import com.example.skycast.model.route.ScreenRout
import com.example.skycast.model.util.AppConstants
import com.example.skycast.splash.SplashScreen
import com.example.skycast.viewmodel.LocationViewModel
import com.example.skycast.viewmodel.WeatherFactory
import com.example.skycast.viewmodel.WeatherViewModel
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text


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
    val weatherInfo by viewModel.weatherInfo.collectAsState()
    //hande bottom bar
    val navController = rememberNavController()
    val bottomBarRoutes = listOf("alert", "favourite", "setting","home_screen")
    // Check current route
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        if (fineGranted || coarseGranted) {
            locationViewModel.fetchLocation()
        } else {
            locationViewModel.setPermissionError("Location permission denied. Please grant location access.")
        }
    }
    Scaffold(
        bottomBar = {
            if (currentRoute in bottomBarRoutes) {
                BottomNavigationBar(navController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = ScreenRout.SplashScreenRoute.route,
            modifier = Modifier.fillMaxSize()
                .padding(innerPadding)
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
                        val fineLocationPermission = Manifest.permission.ACCESS_FINE_LOCATION
                        val coarseLocationPermission = Manifest.permission.ACCESS_COARSE_LOCATION

                        val fineGranted = ContextCompat.checkSelfPermission(
                            context, fineLocationPermission
                        ) == PackageManager.PERMISSION_GRANTED

                        val coarseGranted = ContextCompat.checkSelfPermission(
                            context, coarseLocationPermission
                        ) == PackageManager.PERMISSION_GRANTED

                        when {
                            fineGranted || coarseGranted -> {
                                locationViewModel.fetchLocation()
                            }

                            else -> {
                                permissionLauncher.launch(
                                    arrayOf(fineLocationPermission, coarseLocationPermission)
                                )
                            }
                        }
                    }
                }

                // UI for Location and Permission States
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // GPS Disabled Handling
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

                    // Loading Indicator
                    if (locationState.isLoading) {
                        CircularProgressIndicator()
                    }

                    // Error Handling
                    locationState.error?.let { error ->
                        Text(
                            text = error,
                            color = Color.Red,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }

                // Safe Weather Fetching
                locationState.latitude?.let { lat ->
                    locationState.longitude?.let { lon ->
                        LaunchedEffect(lat, lon) {
                            viewModel.getCurrentWeather(
                                lat = lat,
                                lon = lon,
                                lang = "en",
                                units = AppConstants.WEATHER_UNIT
                            )
                            viewModel.getWeatherInfo(lat = lat, lon = lon)
                            Log.i("TAG", "AppNavigation: ${weatherInfo.name}")
                        }
                    }
                }

                // Weather Display Logic
                when (val currentWeather = weather) {
                    is WeatherResult.Loading -> {
                        CircularProgressIndicator()
                    }

                    is WeatherResult.Success -> {
                        WeatherScreen(currentWeather.data, weatherInfo)
                    }

                    is WeatherResult.Failure -> {
                        Text(
                            text = "Weather Error: ${currentWeather.error.message}",
                            color = Color.Red
                        )
                    }
                }
            }
            composable("alert") {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Alert Screen")
                }
            }

            composable("favourite") {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Favourite Screen")
                }
            }

            composable("setting") {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Settings Screen")
                }
            }

        }
        }
    }



@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        BottomNavItem("Home", "home_screen", Icons.Default.Home),
        BottomNavItem("Alert", "alert", Icons.Default.Add),
        BottomNavItem("Favourite", "favourite", Icons.Default.LocationOn),
        BottomNavItem("Setting", "setting", Icons.Default.Settings)
    )
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo("home") { inclusive = false }
                            launchSingleTop = true
                        }
                    }
                }
            )
        }
    }

}

data class BottomNavItem(
    val label: String,
    val route: String,
    val icon: ImageVector
)

