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
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.CircularProgressIndicator


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WeatherScreen(weather: WeatherResponse) {
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        Text("Timezone: ${weather.timezone}", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text("Latitude: ${weather.lat}, Longitude: ${weather.lon}", fontSize = 14.sp)

        Spacer(modifier = Modifier.height(16.dp))

        // 🌡️ Current Weather
        Text("Current Weather", fontSize = 18.sp, fontWeight = FontWeight.Medium)
        Text("${weather.timezone}", fontSize = 18.sp, fontWeight = FontWeight.Medium)
        CurrentWeatherSection(current = weather.current)


        Spacer(modifier = Modifier.height(16.dp))

        // 🕐 Hourly Forecast (first 5)
        Text("Hourly Forecast", fontSize = 18.sp, fontWeight = FontWeight.Medium)
        LazyRow {
            items(weather.hourly) { hour ->
                HourlyWeatherCard(hour)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 📅 Daily Forecast (first 3)
        Text("Daily Forecast", fontSize = 18.sp, fontWeight = FontWeight.Medium)
        LazyColumn {
            items(weather.daily) { day ->
                DailyWeatherItem(day)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 🚨 Alerts
        weather.alerts?.let { alerts ->
            if (alerts.isNotEmpty()) {
                Text("Weather Alerts", fontSize = 18.sp, fontWeight = FontWeight.Medium, color = Color.Red)
                alerts.forEach { alert ->
                    Text("- ${alert.event}", color = Color.Red)
                }
            }
        }
    }
}
@Composable
fun CurrentWeatherSection(current: CurrentWeather) {
    Column(modifier = Modifier.padding(8.dp)) {
        Text("Temp: ${current.temp}°C")
        Text("Humidity: ${current.humidity}%")
        Text("Wind Speed: ${current.wind_speed} m/s")
        Text("Description: ${current.weather.firstOrNull()?.description ?: "N/A"}")
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
