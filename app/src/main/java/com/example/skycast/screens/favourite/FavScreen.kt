package com.example.skycast.screens.favourite


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.DismissValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.skycast.R
import com.example.skycast.model.pojo.WeatherResponse
import com.example.skycast.model.util.Utils.WeatherIcon
import com.example.skycast.ui.theme.PrimaryColor
import com.example.skycast.ui.theme.TertiaryColor
import com.example.skycast.viewmodel.WeatherViewModel
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.rememberDismissState
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.material.*
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Alignment.Companion.BottomEnd
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalContext
import com.example.skycast.model.sharedpreferences.SharedManager
import com.example.skycast.model.util.AppConstants
import com.example.skycast.model.util.Utils

@Composable
fun FavWeatherScreen(
    weather: List<WeatherResponse>,
    onNavigateToLocation: () -> Unit,
    onNavigateToHome: (WeatherResponse?) -> Unit,
    viewModel: WeatherViewModel
) {
    viewModel.getFavoriteWeathers()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    var weatherList by remember { mutableStateOf(weather) }

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
        Text(text = "${R.string.favourite}", color = Color.White,style = androidx.compose.material3.MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(12.dp))
        if (weatherList.isEmpty()) {
            EmptyState(onNavigateToLocation)
        } else {
            viewModel.getFavoriteWeathers()
            Spacer(modifier = Modifier.height(20.dp))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                items(weatherList) { item ->
                    WeatherCard(item, { onNavigateToHome(it) }) {
                        weatherList = weatherList - item
                        coroutineScope.launch {
                            val result = viewModel.deleteFavorite(item) { success ->
                                if (!success) {
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Delete Failed")
                                    }
                                }
                            }

                            val undoResult = snackbarHostState.showSnackbar(
                                message = "Deleted ${item.name}",
                                actionLabel = "Undo",
                                duration = SnackbarDuration.Short
                            )
                            if (undoResult == SnackbarResult.ActionPerformed) {
                                weatherList = weatherList + item // Restore item on Undo
                                viewModel.insertCurrentWeatherToDb(item) // Re-add to database
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.End
            ) {
                IconButton(
                    onClick = { onNavigateToLocation() },
                    modifier = Modifier
                        .size(90.dp)
                        .padding(16.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.location),
                        contentDescription = stringResource(id = R.string.go_to_location_screen),
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
    SnackbarHost(hostState = snackbarHostState)
}


@Composable
fun WeatherCard(
    data: WeatherResponse,
    onNavigateToHome: (WeatherResponse) -> Unit,
    onDelete: () -> Unit
) {
    val lang = SharedManager.getSettings()?.lang?:AppConstants.LANG_EN
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .padding(vertical = 6.dp, horizontal = 12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(PrimaryColor.value).copy(alpha = 0.25f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = { onNavigateToHome(data) }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(PrimaryColor.value).copy(alpha = 0.5f),
                            Color(TertiaryColor.value).copy(alpha = 0.5f)
                        )
                    )
                )
                .padding(16.dp)
        ) {
            // Delete icon positioned at the top-left
            IconButton(
                onClick = onDelete,
                modifier = Modifier
                    .size(40.dp)
                    .align(Alignment.TopEnd) // Align top-left
            ) {
                Icon(
                    imageVector = Icons.Filled.Clear, // "X" icon
                    contentDescription = "Delete weather card",
                    tint = Color.White
                )
            }

            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        if(lang == "ar"){"${Utils.getAddressArabic(LocalContext.current,data.lat,data.lon)}"}
                        else{"${data.name}°"},
                        fontSize = 32.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        if(lang == "ar"){"${Utils.englishNumberToArabicNumber((data.current?.temp ?: 0.0).toString())}"}
                        else{"${data.current?.temp ?: 0.0}"}
                        , fontSize = 16.sp, color = Color.White)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    WeatherIcon(data.current?.weather?.get(0)?.icon ?: "", 42.dp)
                    Text(
                        data.current?.weather?.get(0)?.description ?: " ",
                        fontSize = 14.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}
@Composable
fun EmptyState(onNavigateToLocation: () -> Unit) {
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
            .padding(16.dp)
    ){
        Image(
            painter = painterResource(id = R.drawable.emptyfav),
            contentDescription = "Weather House",
            modifier = Modifier
                .size(300.dp)
                .align(Alignment.Center)
        )
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.End
        ) {
            IconButton(
                onClick = { onNavigateToLocation() },
                modifier = Modifier
                    .size(90.dp)
                    .padding(16.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.location),
                    contentDescription = stringResource(id = R.string.go_to_location_screen),
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

