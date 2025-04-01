package com.example.skycast.model.route
import android.os.Parcelable
import com.example.skycast.model.pojo.WeatherResponse
import kotlinx.android.parcel.Parcelize
import kotlinx.serialization.Serializable

@Serializable
sealed class ScreenRout(val route: String) {
    @Serializable
    object SplashScreenRoute : ScreenRout("splash_screen")
    @Parcelize
    data class HomeScreenRoute(val weatherJson: WeatherResponse) : ScreenRout("home_screen"),
        Parcelable

    @Serializable
    object FavScreenRoute : ScreenRout("Fav_screen")
    @Serializable
    object LocationcreenRoute : ScreenRout("location")
}