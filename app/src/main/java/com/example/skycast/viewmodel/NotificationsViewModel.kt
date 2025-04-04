package com.example.skycast.viewmodel

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.skycast.model.pojo.AlertSettings
import com.example.skycast.model.pojo.MyAlert
import com.example.skycast.model.repositries.WeatherRepositry
import com.example.skycast.model.result.LocalDataStateAlerts
import com.example.skycast.screens.notifications.WeatherManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class NotificationsViewModel(
    private val repository: WeatherRepositry,
    private val workManager: WeatherManager,
    private val context: Context
) : ViewModel() {
    private val _scheduledAlerts = MutableStateFlow<List<MyAlert>>(emptyList())
    val scheduledAlerts: StateFlow<List<MyAlert>> = _scheduledAlerts.asStateFlow()

    init {
        loadAlerts()
    }


    fun updateAlerts() {
        loadAlerts()
    }

    private fun loadAlerts() {
        viewModelScope.launch {
            try {
                repository.getAlerts().collect { alerts ->
                    // Update UI with valid alerts
                    _scheduledAlerts.value = alerts
                    deleteFromList()
                }
            } catch (e: Exception) {
                Log.e("NotificationsVM", "Error loading alerts", e)
            }
        }
    }
    fun deleteFromList( ){
        val currentTime = System.currentTimeMillis()

        val updatedAlerts = _scheduledAlerts.value.filter { alert ->
            val duration = alert.duration ?: 10
            val startTime = alert.startTime ?: 0
            val endTime = startTime + duration

            endTime > currentTime // Keep only non-expired alerts
        }

        _scheduledAlerts.value = updatedAlerts

    }

    fun scheduleAlert(settings: MyAlert) {
        viewModelScope.launch {
            try {
                if (!Settings.canDrawOverlays(context)) {
                    requestOverlayPermission()
                    return@launch
                }
                // Add insertion result check
                Log.d("InsertDebug", "Scheduling alert: ${settings.dbId}")
                val result = repository.insertAlert(settings)
                Log.d("InsertDebug", "Insert result: $result")
                if (result > 0) {
                    workManager.scheduleWeatherAlert(
                        settings.id?:"",
                        settings.duration?:10,
                        settings.useDefaultSound?:false
                    )
                    // Refresh alerts after successful insertion
                    loadAlerts()
                } else {
                    Log.e("NotificationsVM", "Failed to insert alert")
                }
            } catch (e: Exception) {
                Log.e("NotificationsVM", "Error scheduling alert", e)
                // Consider updating UI state to show error
            }
        }
    }

    private fun requestOverlayPermission() {
        if (!Settings.canDrawOverlays(context)) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:${context.packageName}")
            ).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        }
    }

    fun cancelAlert(id: String) {
        viewModelScope.launch {
            try {
                scheduledAlerts.value.find { it.id == id }?.let { alert ->
                    val result = repository.deleteAlert(alert)
                    if (result > 0) {
                        workManager.cancelWeatherAlert(alert.id?:"")
                        // Refresh alerts after successful deletion
                        loadAlerts()
                    } else {
                        Log.e("NotificationsVM", "Failed to delete alert")
                    }
                }
            } catch (e: Exception) {
                Log.e("NotificationsVM", "Error canceling alert", e)
            }
        }
    }

    private suspend fun deleteAlert(alert: MyAlert) {
        repository.deleteAlert(alert)
        workManager.cancelWeatherAlert(alert.id.toString())
    }

    class Factory(
        private val repository: WeatherRepositry,
        private val workManager: WeatherManager,
        private val context: Context
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(NotificationsViewModel::class.java)) {
                return NotificationsViewModel(repository, workManager, context) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
