package com.example.skycast.screens.notifications

import android.content.Context
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.skycast.model.local.LocalDataSource
import com.example.skycast.model.local.WeatherDataBse
import com.example.skycast.model.remote.RemoteDataSourceImpl
import com.example.skycast.model.repositries.WeatherRepositry
import java.util.concurrent.TimeUnit

object WorkerUtils {

    // Lazily initialize the repository
    @Volatile
    private var repository: WeatherRepositry? = null

    fun getRepository(context: Context): WeatherRepositry {
        return repository ?: synchronized(this) {
            repository ?: createRepository(context).also { repository = it }
        }
    }

    private fun createRepository(context: Context): WeatherRepositry {
        return WeatherRepositry(RemoteDataSourceImpl(),LocalDataSource(context))
    }

}
