package com.example.skycast.model
import kotlinx.serialization.Serializable

@Serializable
sealed class ScreenRout(val route: String) {
    @Serializable
    object SplashScreenRoute : ScreenRout ("splash_screen")
    @Serializable
    object HomeScreenRoute : ScreenRout ("home_screen")
}