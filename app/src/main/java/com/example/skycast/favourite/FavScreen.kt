package com.example.skycast.favourite


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.skycast.R
import com.example.skycast.ui.theme.PrimaryColor
import com.example.skycast.ui.theme.PurpleGrey40
import com.example.skycast.ui.theme.SecondaryColor
import com.example.skycast.ui.theme.TertiaryColor



@Composable
fun FavWeatherScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(PrimaryColor.value),
                        Color(TertiaryColor.value)
                    )
                )
            )
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))
        Column {
            SearchBar() // ✅ Valid here
        }
        Spacer(modifier = Modifier.height(20.dp))
        WeatherList()
    }
}
@Preview(showSystemUi = true)
@Composable
fun MyScreenPreview() {
    FavWeatherScreen()
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar() {
    var query = remember { mutableStateOf("") }

    OutlinedTextField(
        value = query.value,
        onValueChange = { query.value = it },
        placeholder = {
            Text("Search for a city", color = Color.White)
        },
        leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = null)
        },
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(TertiaryColor.value)),
        colors = TextFieldDefaults.outlinedTextFieldColors(
           disabledTextColor = Color.White,
            disabledPlaceholderColor = Color.White.copy(alpha = 0.6f),
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,
            cursorColor = Color.White,
           disabledLeadingIconColor = Color.White
        )
    )
}

@Composable
fun WeatherList() {
    val weatherItems = listOf(
        WeatherData("Montreal, Canada", 24, "Mostly sunny", R.drawable.d02),
        WeatherData("Colrado, Canada", 18, "Rain thunderstorm", R.drawable.d02),
        WeatherData("Sydney, Australia", 30, "Partly cloudy", R.drawable.d02),
        WeatherData("Tokyo, Japan", 33, "Mid rain", R.drawable.d02)
    )

    LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        items(weatherItems) { item ->
            WeatherCard(item)
        }
    }
}
@Composable
fun WeatherCard(data: WeatherData) {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .padding(vertical = 6.dp, horizontal = 12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(PrimaryColor.value).copy(alpha = 0.25f) // subtle background
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(PrimaryColor.value),
                            Color(TertiaryColor.value)
                        )
                    )
                )
                .padding(16.dp) // Padding inside the card, adjust as needed
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "${data.temp}°",
                        fontSize = 32.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text("H: 26°  L: 16°", fontSize = 14.sp, color = Color.White)
                    Text(data.city, fontSize = 16.sp, color = Color.White)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = painterResource(id = data.iconRes),
                        contentDescription = null,
                        modifier = Modifier.size(48.dp)
                    )
                    Text(data.description, fontSize = 14.sp, color = Color.White)
                }
            }
        }
    }
}
data class WeatherData(
    val city: String,
    val temp: Int,
    val description: String,
    val iconRes: Int
)
