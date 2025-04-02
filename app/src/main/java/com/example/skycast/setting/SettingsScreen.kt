package com.example.skycast.setting

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.benchmark.json.BenchmarkData
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.skycast.model.local.LocalDataSource
import com.example.skycast.model.pojo.Settings
import com.example.skycast.model.pojo.WeatherResponse
import com.example.skycast.model.remote.RemoteDataSourceImpl
import com.example.skycast.model.repositries.WeatherRepositry
import com.example.skycast.model.sharedpreferences.SharedManager
import com.example.skycast.model.util.AppConstants
import com.example.skycast.ui.theme.PrimaryColor
import com.example.skycast.ui.theme.TertiaryColor
import com.example.skycast.viewmodel.SettingsViewModel


@Composable
fun SettingsScreen(onNavToHome:()->Unit, settingsViewModel: SettingsViewModel = viewModel()) {
    val settings by settingsViewModel.settings.collectAsState(initial = Settings())

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(TertiaryColor.value),
                        Color(PrimaryColor.value)

                    )
                ),
                shape = RoundedCornerShape(16.dp)
            ),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Settings", style = MaterialTheme.typography.headlineMedium)

        // Location Selection (GPS or Map)
        LocationSelection(settings) { isMapSelected ->
            settingsViewModel.saveSettings(settings.copy(isMap = isMapSelected))
        }

        // Temperature Unit Selection
        TemperatureUnitSelection(settings) { selectedUnit ->
            settingsViewModel.saveSettings(settings.copy(unit = selectedUnit))
        }

        // Language Selection
        LanguageSelection(settings) { selectedLanguage ->
            settingsViewModel.saveSettings(settings.copy(lang = selectedLanguage))
        }
        // Save Button
        Button(onClick = {
            settingsViewModel.saveSettings(settings)
            onNavToHome()}) {
            Text(text = "Save & Back")
        }
    }
}

@Composable
fun LocationSelection(settings: Settings, onSelectionChange: (Boolean) -> Unit) {
    Column {
        Text(text = "Location Source", style = MaterialTheme.typography.titleMedium)

        listOf("GPS" to false, "Map" to true).forEach { (label, value) ->
            RadioButtonWithLabel(
                text = label,
                selected = settings.isMap == value,
                onSelected = { onSelectionChange(value) }
            )
        }
    }
}

@Composable
fun TemperatureUnitSelection(settings: Settings, onSelectionChange: (String) -> Unit) {
    Column {
        Text(text = "Temperature Unit", style = MaterialTheme.typography.titleMedium)

        listOf(
            "Kelvin" to AppConstants.UNITS_DEFAULT,
            "Fahrenheit" to AppConstants.UNITS_FAHRENHEIT,
            "Celsius" to AppConstants.UNITS_CELSIUS
        ).forEach { (label, value) ->
            RadioButtonWithLabel(
                text = label,
                selected = settings.unit == value,
                onSelected = { onSelectionChange(value) }
            )
        }
    }
}

@Composable
fun LanguageSelection(settings: Settings, onSelectionChange: (String) -> Unit) {
    Column {
        Text(text = "Language", style = MaterialTheme.typography.titleMedium)

        listOf("English" to AppConstants.LANG_EN, "Arabic" to AppConstants.LANG_AR).forEach { (label, value) ->
            RadioButtonWithLabel(
                text = label,
                selected = settings.lang == value,
                onSelected = { onSelectionChange(value) }
            )
        }
    }
}

@Composable
fun RadioButtonWithLabel(text: String, selected: Boolean, onSelected: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(selected = selected, onClick = onSelected)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = selected, onClick = onSelected)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text)
    }
}

//@RequiresApi(Build.VERSION_CODES.O)
//@Preview(showSystemUi = true)
//@Composable
//fun SettingsScreenPreview() {
//    SettingsScreen()
//}
