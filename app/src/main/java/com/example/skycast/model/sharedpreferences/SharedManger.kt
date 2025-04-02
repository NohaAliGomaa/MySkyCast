package com.example.skycast.model.sharedpreferences

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.skycast.model.pojo.Settings
import com.google.gson.Gson

private const val SHARE_KEY = "shareRoom"

object SharedManager {
    private lateinit var sharedPreferences: SharedPreferences
    private val gson = Gson()

    private const val SETTINGS = "SETTINGS"
    private const val ALERT_SETTINGS = "ALERTSETTINGS"

    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
    }

    fun saveSettings(settings: Settings) {
        sharedPreferences.edit()
            .putString(SETTINGS, gson.toJson(settings))
            .apply()
    }

    fun getSettings(): Settings? {
        if (!::sharedPreferences.isInitialized) {
            Log.e("SharedManager", "SharedPreferences not initialized. Call init(context) first.")
            return null
        }
        return sharedPreferences.getString(SETTINGS, null)?.let {
            gson.fromJson(it, Settings::class.java)
        }
    }

//    fun saveAlertSettings(alertSettings: AlertSettings) {
//        sharedPreferences.edit()
//            .putString(ALERT_SETTINGS, gson.toJson(alertSettings))
//            .apply()
//    }
//
//    fun getAlertSettings(): AlertSettings? {
//        return sharedPreferences.getString(ALERT_SETTINGS, null)?.let {
//            gson.fromJson(it, AlertSettings::class.java)
//        }
//    }
}
