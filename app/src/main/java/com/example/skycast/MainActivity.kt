package com.example.skycast

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.net.Uri.encode
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
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
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
import com.example.skycast.model.remote.RemoteDataSourceImpl
import com.example.skycast.model.repositries.WeatherRepositry
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
import androidx.compose.ui.graphics.Brush
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.toRoute
import com.example.skycast.favourite.FavWeatherScreen
import com.example.skycast.map.LocationScreen
import com.example.skycast.model.local.LocalDataSource
import com.example.skycast.model.pojo.WeatherResponse
import com.example.skycast.model.result.LocalDataState
import com.example.skycast.model.util.NetworkUtils
import com.example.skycast.ui.theme.PrimaryColor
import com.example.skycast.ui.theme.SecondaryColor
import com.example.skycast.ui.theme.TertiaryColor
import com.example.skycast.viewmodel.LocationFactory
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException


class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val isOnline = NetworkUtils.isInternetAvailable(this)
        val factory = WeatherFactory(WeatherRepositry(RemoteDataSourceImpl(),LocalDataSource(this)),this)
        val viewModel = ViewModelProvider(this, factory).get(WeatherViewModel::class.java)
        val app = application as MyApp
        val locationFactory = LocationFactory(app)
        val locationViewModel = ViewModelProvider(this, locationFactory).get(LocationViewModel::class.java)

        setContent {
            SkyCastTheme {
                AppNavigation(viewModel,isOnline,locationViewModel)
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation(
    viewModel: WeatherViewModel,
    isOnline : Boolean,
    locationViewModel: LocationViewModel

) {

    val context = LocalContext.current
    val locationState by locationViewModel.locationState.collectAsState()
    val weather by viewModel.weather.collectAsStateWithLifecycle()
    val favWeather by viewModel.favWeather.collectAsStateWithLifecycle()
    val weatherInfo by viewModel.weatherInfo.collectAsState()
    //hande bottom bar
    val navController = rememberNavController()
    val bottomBarRoutes = listOf("alert", "Fav_screen", "setting","home_screen","location")
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
            val currentBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = currentBackStackEntry?.destination?.route ?: ""

            // Show Bottom Navigation Bar only for certain routes
            if (currentRoute in bottomBarRoutes) {
                BottomNavigationBar(navController)
            }
        }
    )
    { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = ScreenRout.SplashScreenRoute.route,
            modifier = Modifier.fillMaxSize()
                .padding(innerPadding)

        ) {
            composable(ScreenRout.SplashScreenRoute.route) {backStackEntry ->
                val weatherObject = navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.get<WeatherResponse>("weatherData")
                SplashScreen {
                    navController.navigate(ScreenRout.HomeScreenRoute(weatherObject?:WeatherResponse()).route) {
                        popUpTo(ScreenRout.SplashScreenRoute.route) { inclusive = true }
                    }
                }
            }

            composable("home_screen") {backStackEntry ->
                val weatherObject = navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.get<WeatherResponse>("weatherData")

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
                if(weatherObject == WeatherResponse() || weatherObject == null) {
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
                            }
                        }
                    }
                }else{
                    viewModel.getCurrentWeather(  lat = weatherObject.lat?:0.0,
                        lon = weatherObject.lon?:0.0,
                        lang = "en",
                        units = AppConstants.WEATHER_UNIT)
                    viewModel.getWeatherInfo( lat = weatherObject.lat?:0.0,
                        lon = weatherObject.lon?:0.0)
                }

                // Weather Display Logic
                when (val currentWeather = weather) {
                    is WeatherResult.Loading -> {
                        CircularProgressIndicator()
                    }

                    is WeatherResult.Success -> {

                        WeatherScreen(currentWeather.data, weatherInfo, isOnline)
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

            composable(ScreenRout.FavScreenRoute.route) {backStackEntry ->

                viewModel.getFavoriteWeathers()
                when (val currentWeather = favWeather) {
                    is LocalDataState.Loading -> {
                        CircularProgressIndicator()
                    }

                    is LocalDataState.Success -> {
                        val favList = currentWeather.data ?: emptyList()

                        FavWeatherScreen(
                            favList,
                             { navController.navigate("location") },
                            { selectedWeather ->
                                navController.currentBackStackEntry?.savedStateHandle?.set("weatherData", selectedWeather?:currentWeather.data)
                                navController.navigate("home_screen")


                            }
                        )
                    }
                    is LocalDataState.Fail-> {
                        Text(
                            text = "Weather Error: ${currentWeather.msg.message}",
                            color = Color.Red
                        )
                    }
                }
            }
            composable("location") {
                LocationScreen(locationViewModel,
                    viewModel
                    ,{ navController.navigate("Fav_screen") })
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
        BottomNavItem("Favourite", "Fav_screen", Icons.Default.Favorite),
        BottomNavItem("Setting", "setting", Icons.Default.Settings)
    )
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(TertiaryColor.value),
                        Color(PrimaryColor.value)
                    )
                )
            )
    ){ NavigationBar(
        containerColor = Color.Transparent // Transparent to show the Box's gradient
    ) {
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
                }, colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White,
                    selectedTextColor = Color.White,
                    indicatorColor = PrimaryColor.copy(alpha = 1f), // Background behind icon
                    unselectedIconColor = Color.LightGray,
                    unselectedTextColor = Color.LightGray
                )

            )
        }
    }
    }

}
data class BottomNavItem(
    val label: String,
    val route: String,
    val icon: ImageVector
)

