package com.example.skycast

import android.os.Bundle
import android.window.SplashScreen
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.skycast.home.HomeScreen
import com.example.skycast.model.ScreenRout
import com.example.skycast.splash.SplashScreen
import com.example.skycast.ui.theme.SkyCastTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                AppNavigation()
            }
        }
    }
}
@Composable
fun AppNavigation() {
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
            HomeScreen()
        }
    }
}

