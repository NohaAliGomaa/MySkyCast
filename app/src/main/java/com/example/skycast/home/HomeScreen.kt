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
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import com.example.skycast.R
import com.example.skycast.ui.theme.PrimaryColor
import com.example.skycast.ui.theme.SecondaryColor
import kotlinx.coroutines.launch
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.*
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.skycast.model.models.Current
import com.example.skycast.model.models.DailyItem
import com.example.skycast.model.models.HourlyItem
import com.example.skycast.model.models.Temp
import com.example.skycast.model.models.WeatherItem
import com.example.skycast.ui.theme.TertiaryColor
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.math.PI
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.PI
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.PI

// Optional for text drawing
import androidx.compose.ui.graphics.nativeCanvas
import android.graphics.Typeface
import androidx.compose.ui.graphics.Paint
import com.example.skycast.model.models.Sys
import com.example.skycast.model.models.WeatherInfo
import java.util.TimeZone


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WeatherScreen(weather: WeatherResponse, weatherInfo: WeatherInfo) {
    val currentWeather = weather.current
    val dailyWeather = weather.daily
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Background Image with House
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.weather_bg),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Image(
                painter = painterResource(id = R.drawable.ic_house),
                contentDescription = "Weather House",
                modifier = Modifier
                    .size(400.dp)
                    .offset(y = 100.dp)
            )
        }

        // Scrollable Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .fillMaxHeight()
                .verticalScroll(scrollState)

        ) {
            Spacer(modifier = Modifier.height(60.dp))

            // Location and Temperature
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "${weatherInfo.name}",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Medium
                )

                Text(
                    "${currentWeather?.temp}°C",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Medium
                )

                Text(
                    currentWeather?.weather?.firstOrNull()?.description ?: "N/A",
                    color = Color.LightGray
                )

                Row(modifier = Modifier.padding(8.dp)) {
                    Text("H°: ${dailyWeather?.get(0)?.temp?.max}°  ", color = Color.White)
                    Text("L°: ${dailyWeather?.get(0)?.temp?.min}°", color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(250.dp)) // Space for house background

            // Scrollable Weather Details
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color(SecondaryColor.value).copy(alpha = 1f),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(16.dp)
            ) {
                // Hourly Forecast
                Text(
                    "Hourly Forecast",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
                LazyRow {
                    items(weather.hourly?.filterNotNull() ?: emptyList()) { hour ->
                        HourlyWeatherCard(hour)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 5-day Forecast
                Text(
                    "5-day forecast",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
                LazyColumn(
                    modifier = Modifier.height(300.dp)
                ) {
                    items((weather.daily?.filterNotNull() ?: emptyList()).take(5)) { day ->
                        ForecastRow(day)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Wind Speed Card
                val windDeg = weather.current?.windDeg ?: 0
                val windSpeed = (weather.current?.windSpeed as? Double ?: 0.0) * 3.6
                val windGust = (weather.current?.windGust as? Double)?.times(3.6)
                WindSpeedCard(
                    windSpeed = windSpeed,
                    windDeg = windDeg,
                    windGust = windGust
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Sunrise, Sunset, and Weather Properties
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        uvandRealFeel((weather.current?.feelsLike?: " ").toString(),(weather.current?.uvi?: " ").toString())
                        sunriseAndSet(weatherInfo.sys?.sunrise ?: 0, weatherInfo.sys?.sunset ?: 0)
                    }

                    weatherProperities(
                        weather.current?.pressure ?: 0,
                        weather.current?.humidity ?: 0,
                        weather.current?.clouds ?: 0
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showSystemUi = true)
@Composable
fun WeatherPreview() {
    val mockWeather = WeatherResponse(
        current = Current(
            temp = 24.0,
            windSpeed = 5.5,
            windDeg = 90,
            windGust = 8.0,
            weather = listOf(
                WeatherItem(
                    main = "Clear",
                    description = "Sunny",
                    icon = "01d"
                )
            )
        ),
        hourly = List(4) {
            HourlyItem(
                dt = 1618317040,
                temp = 22.0,
                windSpeed = 10.5,
                weather = listOf(
                    WeatherItem(
                        main = "Clear",
                        description = "Sunny",
                        icon = "01d"
                    )
                )
            )
        },
        daily = List(4) {
            DailyItem(
                dt = 1618317040,
                temp = Temp(day = 26.0, night = 18.0, max = 27.0, min = 16.0),
                weather = listOf(
                    WeatherItem(
                        main = "Cloudy",
                        description = "Overcast clouds",
                        icon = "02d"
                    )
                )
            )
        }
    )
    val weatherInfo = WeatherInfo(name = "Ismaillia", sys = Sys("", 5, 18))
    WeatherScreen(mockWeather, weatherInfo)
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HourlyWeatherCard(hour: HourlyItem) {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .padding(vertical = 6.dp, horizontal = 12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.05f) // subtle background
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .width(60.dp)
                .padding(horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = formatTime(hour.dt), // Format like "01:00", "Now"
                 fontSize = 12.sp,
                 color = Color.White
            )

            Spacer(modifier = Modifier.height(4.dp))

            WeatherIcon(iconCode = hour.weather?.firstOrNull()?.icon ?: "01d", size = 24.dp)

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${hour.temp}°",
                style = MaterialTheme.typography.bodyMedium
                , color = Color.White
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${hour.windSpeed}km/h", // Add wind speed in your model
                style = MaterialTheme.typography.labelSmall
                , color = Color.White
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ForecastRow(day: DailyItem) {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp, horizontal = 12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.05f) // subtle background
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                WeatherIcon(iconCode = day.weather?.firstOrNull()?.icon ?: "01d", size = 24.dp)

                Spacer(Modifier.width(8.dp))

                Column {
                    Text(
                        text = formatDate(day.dt),
                        style = MaterialTheme.typography.bodyMedium
                        , color = Color.White
                    )
                    Text(
                        text = day.weather?.firstOrNull()?.main ?: "Clear",
                        style = MaterialTheme.typography.bodySmall
                        , color = Color.White
                    )
                }
            }

            Text(
                text = "${day.temp?.day}° / ${day.temp?.night}°",
                style = MaterialTheme.typography.bodyMedium
                , color = Color.White
            )
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
fun formatTime(timestamp: Int?): String {
    return timestamp?.let {
        val time = Date(it * 1000L)
        SimpleDateFormat("h:mm a", Locale.getDefault()).format(time)
    } ?: ""
}

@RequiresApi(Build.VERSION_CODES.O)
fun formatDate(timestamp: Int?): String {
    return timestamp?.let {
        val date = Date(it * 1000L)
        SimpleDateFormat("EEEE, MMM d", Locale.getDefault()).format(date)
    } ?: ""
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun WeatherIcon(iconCode: String, size: Dp = 24.dp) {
    val iconUrl = "https://openweathermap.org/img/wn/${iconCode}@4x.png"

    GlideImage(
        model = iconUrl,
        contentDescription = "Weather Icon",
        modifier = Modifier.size(size),
        contentScale = ContentScale.Fit
    )
}
@Composable
fun WindSpeedCard(windSpeed: Double, windDeg: Int, windGust: Double?) {
    val directionLabel = degToCompassDirection(windDeg)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.05f) // subtle background
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Text Info
            Column {
                Text(
                    text = directionLabel,
                    style = MaterialTheme.typography.titleMedium.copy(color = Color.White)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${String.format("%.1f", windSpeed)} km/h",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
                windGust?.let {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Gusts up to ${String.format("%.1f", it)} km/h",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color.White)
                    )
                }
            }

            // Compass
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val center = this.center
                    val radius = size.minDimension / 2.2f
                    val arrowLength = size.minDimension / 3f

                    // Draw outer circle
                    drawCircle(
                        color = Color.White,
                        style = Stroke(width = 6f)
                    )

                    // Draw cardinal direction labels
                    val labelOffset = radius - 10.dp.toPx()
                    val directions = listOf("N" to 270f, "E" to 0f, "S" to 90f, "W" to 180f)
                    val textPaint = Paint().asFrameworkPaint().apply {
                        isAntiAlias = true
                        color = android.graphics.Color.WHITE
                        textSize = 12.sp.toPx()
                        textAlign = android.graphics.Paint.Align.CENTER
                        typeface = Typeface.DEFAULT_BOLD
                    }

                    directions.forEach { (label, angle) ->
                        val rad = angle.toRadians()
                        val x = center.x + labelOffset * cos(rad)
                        val y = center.y + labelOffset * sin(rad) + 8.dp.toPx() // vertical centering
                        drawContext.canvas.nativeCanvas.drawText(label, x, y, textPaint)
                    }
// Draw wind arrow with head
                    val angleRad = windDeg.toFloat().toRadians()

// Main arrow line
                    val endX = center.x + arrowLength * cos(angleRad)
                    val endY = center.y + arrowLength * sin(angleRad)
                    drawLine(
                        color = Color.White,
                        start = center,
                        end = Offset(endX, endY),
                        strokeWidth = 4f,
                        cap = StrokeCap.Round
                    )

// Draw arrowhead
                    val arrowHeadLength = 12.dp.toPx()
                    val arrowHeadAngle = 25f.toRadians()

// Left side of arrowhead
                    val leftX = endX - arrowHeadLength * cos(angleRad - arrowHeadAngle)
                    val leftY = endY - arrowHeadLength * sin(angleRad - arrowHeadAngle)

// Right side of arrowhead
                    val rightX = endX - arrowHeadLength * cos(angleRad + arrowHeadAngle)
                    val rightY = endY - arrowHeadLength * sin(angleRad + arrowHeadAngle)

// Draw left and right lines
                    drawLine(
                        color = Color.White,
                        start = Offset(endX, endY),
                        end = Offset(leftX, leftY),
                        strokeWidth = 3f,
                        cap = StrokeCap.Round
                    )
                    drawLine(
                        color = Color.White,
                        start = Offset(endX, endY),
                        end = Offset(rightX, rightY),
                        strokeWidth = 3f,
                        cap = StrokeCap.Round
                    )
                }
            }
        }
    }
}

private fun Float.toRadians(): Float = (this * PI / 180f).toFloat()

private fun degToCompassDirection(deg: Int): String {
    return when ((deg % 360 + 22) / 45) {
        0 -> "North"
        1 -> "North-East"
        2 -> "East"
        3 -> "South-East"
        4 -> "South"
        5 -> "South-West"
        6 -> "West"
        7 -> "North-West"
        else -> "North"
    }
}

@Composable
fun sunriseAndSet(rise : Int, set : Int){
    Card(
        modifier = Modifier
            .padding(8.dp)
            .width(140.dp)
            .height(90.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.05f) // subtle background
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Text Info
            Column {
                Text(
                    text = "${unixToHour(rise.toLong())}  Sunrise",
                    style = MaterialTheme.typography.titleMedium.copy(color = Color.White)
                    , fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${unixToHour(set.toLong())}  Sunset",
                    style = MaterialTheme.typography.titleMedium.copy(color = Color.White)
                    , fontSize = 16.sp
                )
            }
        }
    }
}

fun unixToHour(unixTime: Long): String {
    val date = Date(unixTime * 1000)
    val sdf = SimpleDateFormat("h:mm", Locale.getDefault())
    sdf.timeZone = TimeZone.getDefault()
    return sdf.format(date)
}
@Composable
fun weatherProperities(pressure : Int, humidity : Int, clouds: Int){
    Card(
        modifier = Modifier
            .padding(12.dp)
            .width(180.dp)
            .height(150.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.05f) // subtle background
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Text Info
            Column {
                Text(
                    text = "Pressure  ${pressure}",
                    style = MaterialTheme.typography.titleMedium.copy(color = Color.White)
                    , fontSize = 16.sp
                )
                Divider(
                    color = Color.White.copy(alpha = 0.5f),
                    thickness = 1.dp,
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .fillMaxWidth(0.8f)
                )
                Text(
                    text = "Humidity  ${humidity}",
                    style = MaterialTheme.typography.titleMedium.copy(color = Color.White)
                    , fontSize = 16.sp
                )
                Divider(
                    color = Color.White.copy(alpha = 0.5f),
                    thickness = 1.dp,
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .fillMaxWidth(0.8f)
                )
                Text(
                    text = "Clouds  ${clouds}",
                    style = MaterialTheme.typography.titleMedium.copy(color = Color.White)
                    , fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
fun uvandRealFeel(real : String, uv : String ){
    Card(
        modifier = Modifier
            .padding(8.dp)
            .width(140.dp)
            .height(50.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.05f) // subtle background
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Text Info
            Column {
                Text(
                    text = "Real feel ${real} ",
                    style = MaterialTheme.typography.titleMedium.copy(color = Color.White)
                    , fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}
