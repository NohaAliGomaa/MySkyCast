package com.example.skycast

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.skycast.home.WeatherScreen
import com.example.skycast.model.remote.WeatherRemoteDataSourceImpl
import com.example.skycast.model.repositries.WeatherRepositry
import com.example.skycast.model.result.WeatherResult
import com.example.skycast.model.route.ScreenRout
import com.example.skycast.splash.SplashScreen
import com.example.skycast.viewmodel.WeatherFactory
import com.example.skycast.viewmodel.WeatherViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val factory= WeatherFactory(
            WeatherRepositry(WeatherRemoteDataSourceImpl())
        )
        val viewModel= ViewModelProvider(this,factory).get(WeatherViewModel::class)
        setContent {
            MaterialTheme {
                AppNavigation(viewModel)
            }
        }
    }
}
@Composable
fun AppNavigation(viewModel: WeatherViewModel) {
    val weather by viewModel.weather.collectAsStateWithLifecycle()
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = ScreenRout.SplashScreenRoute.route,
        modifier = Modifier.fillMaxSize()
    ) {

        composable(route = ScreenRout.SplashScreenRoute.route) {
            SplashScreen {
                navController.navigate(ScreenRout.HomeScreenRoute.route) {
                    popUpTo(ScreenRout.SplashScreenRoute.route) { inclusive = true }
                }
            }
        }

        composable(route = ScreenRout.HomeScreenRoute.route) {
            LaunchedEffect(Unit) {
                viewModel.getCurrentWeather()
            }
            when (weather) {
                is WeatherResult.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                }

                is WeatherResult.Success -> {
                    val currentWeather = (weather as WeatherResult.Success).data
                    WeatherScreen(currentWeather)
                }

                is WeatherResult.Failure -> {
                    val errorMessage =
                        (weather as WeatherResult.Failure).error.message ?: "Unknown Error"
                    Log.i("TAG", "AppNavigation: $errorMessage")
                }
            }
        }
    }
}

