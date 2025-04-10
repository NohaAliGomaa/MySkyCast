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
        val json = gson.toJson(settings)
        sharedPreferences.edit()
            .putString(SETTINGS, json)
            .apply()
        Log.i("SharedManager", "Settings saved: $json")
    }

    fun getSettings(): Settings? {
        if (!::sharedPreferences.isInitialized) {
            Log.e("SharedManager", "SharedPreferences not initialized. Call init(context) first.")
            return null
        }
        val settingsJson = sharedPreferences.getString(SETTINGS, null)
        if (settingsJson != null) {
            Log.d("SharedManager", "Settings retrieved: $settingsJson")
            return gson.fromJson(settingsJson, Settings::class.java)
        } else {
            Log.d("SharedManager", "No settings found.")
            return null
        }
    }
    fun updateUnit(unit :String){

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
//class SharedManager(private val context: Context) {
//
//    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
//
//    fun putBoolean(key: String, value: Boolean) {
//        sharedPreferences.edit().putBoolean(key, value).apply()
//    }
//
//    fun getBoolean(key: String, defaultValue: Boolean): Boolean {
//        return sharedPreferences.getBoolean(key, defaultValue)
//    }
//
//    fun putString(key: String, value: String) {
//        sharedPreferences.edit().putString(key, value).apply()
//    }
//
//    fun getString(key: String, defaultValue: String): String {
//        return sharedPreferences.getString(key, defaultValue) ?: defaultValue
//    }
//}
