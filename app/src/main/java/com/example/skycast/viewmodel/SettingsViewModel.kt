package com.example.skycast.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.skycast.model.pojo.Settings
import com.example.skycast.model.repositries.WeatherRepositry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(var repository:  WeatherRepositry) : ViewModel() {
    private val _settings = MutableStateFlow(Settings())
    val settings: StateFlow<Settings> = _settings
    init {
        // Load settings from the repository (or SharedPreferences)
        loadSettings()
    }
    private fun loadSettings() {
        viewModelScope.launch {
            val settings = repository.getSettings()?:Settings()// Fetch from SharedPreferences or DataStore
            _settings.value = settings
        }
    }
    fun saveSettings(settings: Settings?){
        viewModelScope.launch {
            repository.saveSettings(settings?:Settings()) // Save to SharedPreferences or DataStore
            _settings.value = settings?:Settings()
        }
    }
}
class SettingsViewModelFactory(val repository: WeatherRepositry): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>) : T{
        return if (modelClass.isAssignableFrom(SettingsViewModel::class.java))
        {
            SettingsViewModel(repository) as T
        }
        else{
            throw java.lang.IllegalArgumentException("View modle class not found")
        }
    }
}