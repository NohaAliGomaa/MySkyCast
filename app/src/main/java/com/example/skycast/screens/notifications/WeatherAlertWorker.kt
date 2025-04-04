package com.example.skycast.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.skycast.MainActivity
import com.example.skycast.R
import com.example.skycast.model.util.NetworkUtils
import com.example.skycast.screens.notifications.AlertReceiver
import com.example.skycast.screens.notifications.WorkerUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit


class WeatherAlertWorker(
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    private val notificationManager = context.getSystemService(NotificationManager::class.java)

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val alertId = inputData.getString(EXTRA_ALERT_ID) ?: return@withContext Result.failure()
            val useDefaultSound = inputData.getBoolean("useDefaultSound", true)

            createNotificationChannel()
            val weatherInfo = getWeatherInfo()
            showNotification(alertId, useDefaultSound, weatherInfo)

            Result.success()
        } catch (_: Exception) {
            Result.failure()
        }
    }

    private suspend fun getWeatherInfo(): WeatherInfo? {
        return try {
            WorkerUtils.getRepository(context)
                .getCurrentWeathers().firstOrNull()?.firstOrNull()?.let { forecast ->
                WeatherInfo(
                    temperature = forecast.current?.temp?.toInt(),
                    description = forecast.current?.weather?.firstOrNull()?.description
                )
            }
        } catch (_: Exception) {
            null
        }
    }

    private fun showNotification(
        alertId: String,
        useDefaultSound: Boolean,
        weatherInfo: WeatherInfo?
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            action = "WEATHER_ALERT"
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            alertId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val dismissIntent = Intent(context, AlertReceiver::class.java).apply {
            action = ACTION_DISMISS_ALERT
            putExtra(EXTRA_ALERT_ID, alertId)
        }

        val dismissPendingIntent = PendingIntent.getBroadcast(
            context,
            alertId.hashCode(),
            dismissIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val isConnected = NetworkUtils.isInternetAvailable(context)
        val contentText = if (weatherInfo != null) {
            if (isConnected) {
                "🌡️ Temperature: ${weatherInfo.temperature}°\n🌤️ Condition: ${weatherInfo.description?.capitalize()}"
            } else {
                "🌡️ Temperature: ${weatherInfo.temperature}°\n🌤️ Condition: ${weatherInfo.description?.capitalize()}\n⚠️ This data is expired. Check your connection and try Aurora again."
            }
        } else {
            "🌦️ Weather conditions update available"
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.alarm)
            .setContentTitle("SkyCast")
            .setContentText(contentText)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .addAction(android.R.drawable.ic_delete,
                "Dismiss", dismissPendingIntent)
            .setStyle(NotificationCompat.BigTextStyle().bigText(contentText))
            .apply {
                if (useDefaultSound) {
                    setDefaults(NotificationCompat.DEFAULT_ALL)
                }
            }
            .build()

        notificationManager?.notify(alertId.hashCode(), notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Weather Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Weather alert notifications"
                enableLights(true)
                enableVibration(true)
                setShowBadge(true)
            }
            notificationManager?.createNotificationChannel(channel)
        }
    }

    private data class WeatherInfo(
        val temperature: Int?,
        val description: String?
    )

    companion object {
        const val CHANNEL_ID = "weather_alerts"
        const val ACTION_DISMISS_ALERT = "com.example.aurora.DISMISS_ALERT"
        const val ACTION_SHOW_ALERT = "com.example.aurora.SHOW_ALERT"
        const val EXTRA_ALERT_ID = "alert_id"
    }
}