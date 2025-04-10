package com.example.skycast.screens.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimatable
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.skycast.R
import com.example.skycast.screens.home.WeatherScreen
import com.example.skycast.model.pojo.WeatherResponse
import com.example.skycast.model.result.WeatherResult
import com.example.skycast.ui.theme.PrimaryColor
import com.example.skycast.ui.theme.TertiaryColor
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onFinish: () -> Unit,/*weather :WeatherResult*/) {
    val composition = rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.splash_animation))
    val progress = animateLottieCompositionAsState(
        composition = composition.value,
        iterations = 1,
        speed = 1.0f,
        restartOnPlay = false
    )

    // Trigger navigation after animation finishes (e.g., 2.5 seconds total)
    LaunchedEffect(true) {
        delay(2500)
        onFinish()
    }




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
    ) {
        LottieAnimation(
            composition = composition.value,
            progress = { progress.value },
            modifier = Modifier.size(250.dp)
        )
    }
}

