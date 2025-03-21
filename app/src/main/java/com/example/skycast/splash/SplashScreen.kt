package com.example.skycast.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimatable
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.skycast.R

@Composable
fun SplashScreen(onNavToLogin: () -> Unit) {
    val composition = rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.splash_animation))
    val progress = animateLottieCompositionAsState(
        composition = composition.value,
        iterations = 1, // Play once
        speed = 1.0f,
        restartOnPlay = false
    )

    LaunchedEffect(progress.value) {
        if (progress.value == 1f) {
            onNavToLogin()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        LottieAnimation(
            composition = composition.value,
            progress = { progress.value },
            modifier = Modifier.size(250.dp)
        )
    }
}
