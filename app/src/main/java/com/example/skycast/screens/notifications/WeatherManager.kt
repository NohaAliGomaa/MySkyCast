package com.example.skycast.screens.notifications


import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.skycast.notifications.WeatherAlertWorker
import java.util.concurrent.TimeUnit

class WeatherManager(private val context: Context) {

    fun setupPeriodicWeatherUpdate() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val weatherWork = PeriodicWorkRequestBuilder<WeatherAlertWorker>(
            30, TimeUnit.MINUTES,
            5, TimeUnit.MINUTES
        ).setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WeatherAlertWorker::class.java.simpleName,
            ExistingPeriodicWorkPolicy.UPDATE,
            weatherWork
        )
    }

    fun scheduleWeatherAlert(
        id: String,
        duration: Long,
        useDefaultSound: Boolean
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlertReceiver::class.java).apply {
            action = WeatherAlertWorker.ACTION_SHOW_ALERT
            putExtra(WeatherAlertWorker.EXTRA_ALERT_ID, id)
            putExtra("useDefaultSound", useDefaultSound)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val triggerTime = System.currentTimeMillis() + duration
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
    }

    fun cancelWeatherAlert(id: String) {
        WorkManager.getInstance(context).cancelUniqueWork("weatherAlert_$id")
    }
}
