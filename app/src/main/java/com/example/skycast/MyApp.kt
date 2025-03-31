package com.example.skycast

import android.app.Application
import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.benchmark.json.BenchmarkData
import com.example.skycast.model.util.AppConstants.Companion.PLACES_KEY
import com.google.android.libraries.places.api.Places


class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, PLACES_KEY)
        }
    }
}
