package com.example.skycast.home

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.skycast.model.location.LocationHelper
import com.example.skycast.model.location.LocationState
import com.example.skycast.model.models.CurrentWeather
import com.example.skycast.model.models.DailyWeather
import com.example.skycast.model.models.HourlyWeather
import com.example.skycast.model.models.WeatherResponse
import com.example.skycast.viewmodel.LocationViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Button
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import android.provider.Settings
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.skycast.R
import com.example.skycast.ui.theme.PrimaryColor
import com.example.skycast.ui.theme.SecondaryColor


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WeatherScreen(weather: WeatherResponse) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // 🌄 Background Image
        Image(
            painter = painterResource(id = R.drawable.weather_bg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // 🏠 House Image (Centered vertically)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(60.dp))
                Text(
                    "${weather.timezone}",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Medium
                )

                val currentWeather = weather.current
                val dailyWeather = weather.daily

                Text(
                    "${currentWeather.temp}°C",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    currentWeather.weather.firstOrNull()?.description ?: "N/A",
                    color = Color.LightGray
                )

                Row(modifier = Modifier.padding(8.dp)) {
                    Text("H°: ${dailyWeather.get(0).temp.max}°  ", color = Color.White)
                    Text("L°: ${dailyWeather.get(0).temp.min}°", color = Color.White)
                }


            Image(
                painter = painterResource(id = R.drawable.ic_house),
                contentDescription = "Weather House",
                modifier = Modifier
                    .size(400.dp)
            )
        }

        // 📦 Static Bottom Sheet Overlapping the House Image
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(350.dp)
                .align(Alignment.BottomCenter),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(SecondaryColor.value))
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                // Optional handle bar
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .width(40.dp)
                        .height(4.dp)
                        .background(Color.Gray, shape = RoundedCornerShape(2.dp))
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text("Hourly Forecast", fontSize = 18.sp, fontWeight = FontWeight.Medium)
                LazyRow {
                    items(weather.hourly.take(5)) { hour ->
                        HourlyWeatherCard(hour)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text("Daily Forecast", fontSize = 18.sp, fontWeight = FontWeight.Medium)
                LazyColumn {
                    items(weather.daily.take(3)) { day ->
                        DailyWeatherItem(day)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                weather.alerts?.let { alerts ->
                    if (alerts.isNotEmpty()) {
                        Text(
                            "Weather Alerts",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Red
                        )
                        alerts.forEach { alert ->
                            Text("- ${alert.event}", color = Color.Red)
                        }
                    }
                }
            }
        }
    }

}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HourlyWeatherCard(hour: HourlyWeather) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .width(100.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text("Hour: ${formatTo12HourTime(hour.dt)}")
            Text("Temp: ${hour.temp}°C")
            Text(hour.weather.firstOrNull()?.main ?: "Clear")
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DailyWeatherItem(day: DailyWeather) {
    Card(
        modifier = Modifier
            .padding(vertical = 4.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text("Date: ${formatToDayDate(day.dt)}")
            Text("Day Temp: ${day.temp.day}°C")
            Text("Night Temp: ${day.temp.night}°C")
            Text(day.weather.firstOrNull()?.main ?: "Clear")
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun formatTo12HourTime(unixTimestamp: Long): String {
    val formatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.getDefault())
    val zoneId = ZoneId.systemDefault()
    return Instant.ofEpochSecond(unixTimestamp)
        .atZone(zoneId)
        .format(formatter)
}

@RequiresApi(Build.VERSION_CODES.O)
fun formatToDayDate(unixTimestamp: Long, timeZone: String = "UTC"): String {
    val formatter = DateTimeFormatter.ofPattern("EEE, dd MMM", Locale.getDefault())
    val zoneId = ZoneId.of(timeZone)
    return Instant.ofEpochSecond(unixTimestamp)
        .atZone(zoneId)
        .format(formatter)
}
