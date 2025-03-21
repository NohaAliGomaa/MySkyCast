package com.example.skycast.home

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

//weather: WeatherSummaryResponse
@Composable
fun HomeScreen() {
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp),
//        verticalArrangement = Arrangement.spacedBy(16.dp)
//    ) {
//        Text(
//            text = "Weather Summary",
//            style = MaterialTheme.typography.headlineSmall,
//            fontWeight = FontWeight.Bold
//        )
//
//        Text(
//            text = "Date: ${weather.date}",
//            style = MaterialTheme.typography.bodyMedium
//        )
//
//        Text(
//            text = "Location: ${weather.lat}, ${weather.lon} (TZ: ${weather.tz})",
//            style = MaterialTheme.typography.bodyMedium
//        )
//
//        Text(
//            text = "Units: ${weather.units.capitalize()}",
//            style = MaterialTheme.typography.bodyMedium
//        )
//
//        Divider()

        Text(
            text = "weather.weather_overview",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(top = 8.dp)
        )
 //   }
}
