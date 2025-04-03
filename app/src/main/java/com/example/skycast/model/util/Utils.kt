package com.example.skycast.model.util

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.location.Address
import android.location.Geocoder
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.skycast.model.sharedpreferences.SharedManager
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object Utils {
    fun unixToHour(unixTime: Long): String {
        val date = Date(unixTime * 1000)
        val sdf = SimpleDateFormat("h:mm a", Locale.getDefault())
        sdf.timeZone = TimeZone.getDefault()
        return sdf.format(date)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun formatTime(timestamp: Int?): String {
        return timestamp?.let {
            val time = Date(it * 1000L)
            SimpleDateFormat("h:mm a", Locale.getDefault()).format(time)
        } ?: ""
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun formatTimeArabic(timestamp: Int?): String {
        return timestamp?.let {
            val time = Date(it * 1000L)
            SimpleDateFormat("h:mm a", Locale("ar")).format(time)
        } ?: ""
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun formatDate(timestamp: Int?): String {
        return timestamp?.let {
            val date = Date(it * 1000L)
            SimpleDateFormat("EEEE, MMM d", Locale.getDefault()).format(date)
        } ?: ""
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun formatDateArabic(timestamp: Int?): String {
        return timestamp?.let {
            val date = Date(it * 1000L)
            SimpleDateFormat("EEEE, MMM d", Locale("ar")).format(date)
        } ?: ""
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun formatday(dt:Long?):String{
        return dt?.let {
            val date = Date(it * 1000L)
            SimpleDateFormat("EEEE", Locale.getDefault()).format(date)
        } ?: ""

    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun formatdayArabic(dt:Long?):String{
        return dt?.let {
            val date = Date(it * 1000L)
            SimpleDateFormat("EEEE", Locale("ar")).format(date)
        } ?: ""

    }
    //Alert
    @RequiresApi(Build.VERSION_CODES.O)
    fun formatDateAlert(dt:Long?):String{
        return dt?.let {
            val date = Date(it)
            SimpleDateFormat("EEEE, MMM d", Locale.getDefault()).format(date)
        } ?: ""

    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun formatTimeAlert(dt:Long?):String{
        return dt?.let {
            val time = Date(it )
            SimpleDateFormat("h:mm a", Locale.getDefault()).format(time)
        } ?: ""

    }

    @OptIn(ExperimentalGlideComposeApi::class)
    @Composable
    fun WeatherIcon(iconCode: String, size: Dp = 24.dp) {
        val iconUrl = "https://openweathermap.org/img/wn/${iconCode}@4x.png"

        GlideImage(
            model = iconUrl,
            contentDescription = "Weather Icon",
            modifier = Modifier.size(size),
            contentScale = ContentScale.Fit
        )
    }

    fun degToCompassDirection(deg: Int): String {
        if(SharedManager.getSettings()?.lang == "ar"){
            return when ((deg % 360 + 22) / 45){
                0 -> "شمال"
                1 -> "شمال شرق"
                2 -> "شرق"
                3 -> "جنوب شرق"
                4 -> "جنوب"
                5 -> "جنوب غرب"
                6 -> "غرب"
                7 -> "شمال غرب"
                else -> "شمال"

            }
        }else {
            return when ((deg % 360 + 22) / 45) {
                0 -> "North"
                1 -> "North-East"
                2 -> "East"
                3 -> "South-East"
                4 -> "South"
                5 -> "South-West"
                6 -> "West"
                7 -> "North-West"
                else -> "North"
            }
        }
    }
    fun englishNumberToArabicNumber(number: String): String {
        val arabicNumber = mutableListOf<String>()
        for (element in number.toString()) {
            when (element) {
                '1' -> arabicNumber.add("١")
                '2' -> arabicNumber.add("٢")
                '3' -> arabicNumber.add("٣")
                '4' -> arabicNumber.add("٤")
                '5' -> arabicNumber.add("٥")
                '6' -> arabicNumber.add("٦")
                '7' -> arabicNumber.add("٧")
                '8' -> arabicNumber.add("٨")
                '9' -> arabicNumber.add("٩")
                '0' ->arabicNumber.add("٠")
                '.'->arabicNumber.add(".")
                '-'->arabicNumber.add("-")
                else -> arabicNumber.add(".")
            }
        }
        return arabicNumber.toString()
            .replace("[", "")
            .replace("]", "")
            .replace(",", "")
            .replace(" ", "")
    }
    fun getAddressEnglish(context: Context, lat: Double?, lon: Double?):String{

        var address:MutableList<Address>?=null

        val geocoder= Geocoder(context)
        address =geocoder.getFromLocation(lat!!,lon!!,1)
        if (address?.isEmpty()==true)
        {
            return "Unkown location"
        }
        else if (address?.get(0)?.countryName.isNullOrEmpty())
        {
            return "Unkown Country"
        }
        else if (address?.get(0)?.adminArea.isNullOrEmpty())
        {
            return address?.get(0)?.countryName.toString()

        }        else
            return address?.get(0)?.adminArea.toString()
    }
    fun getAddressArabic(context: Context, lat:Double, lon:Double):String{
        var address:MutableList<Address>?=null

        val geocoder= Geocoder(context,Locale("ar"))
        address =geocoder.getFromLocation(lat,lon,1)

        if (address?.isEmpty()==true)
        {
            return "Unkown location"
        }
        else if (address?.get(0)?.countryName.isNullOrEmpty())
        {
            return "Unkown Country"
        }
        else if (address?.get(0)?.adminArea.isNullOrEmpty())
        {
            return address?.get(0)?.countryName.toString()

        }
        else
            return address?.get(0)?.adminArea.toString()

    }

    @SuppressLint("SimpleDateFormat")
    fun getCurrentDate(): String {
        val currentTime = Calendar.getInstance().time
        val sdf = SimpleDateFormat("dd-MM-yyyy")
        return sdf.format(currentTime)
    }
    @SuppressLint("SimpleDateFormat")
    fun getCurrentTime(): String {
        val currentTime = Calendar.getInstance().time
        val sdf = SimpleDateFormat("HH:mm")
        return sdf.format(currentTime)
    }
    @SuppressLint("SimpleDateFormat")
    fun getCurrentDatePlusOne(): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        val tomorrow = calendar.time
        val sdf = SimpleDateFormat("dd-MM-yyyy")
        return sdf.format(tomorrow)
    }
    @SuppressLint("SimpleDateFormat")
    fun pickedDateFormatDate(dt:Date): String {
        val sdf = SimpleDateFormat("dd-MM-yyyy")
        return sdf.format(dt)
    }
    @SuppressLint("SimpleDateFormat")
    fun pickedDateFormatTime(dt:Date): String {
        val sdf = SimpleDateFormat("HH:mm")
        return sdf.format(dt)
    }
//    @RequiresApi(Build.VERSION_CODES.M)
//    fun canelAlarm(context: Context, alert:String?, requestCode:Int) {
//        var alarmMgr: AlarmManager? = null
//        lateinit var alarmIntent: PendingIntent
//
//        alarmMgr = context.applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
//        alarmIntent = Intent(context.applicationContext, AlarmReciver::class.java).putExtra(
//            Constants.Alert,alert).let { intent ->
//            PendingIntent.getBroadcast(context.applicationContext, requestCode, intent,
//                PendingIntent.FLAG_IMMUTABLE
//            )
//        }
//        alarmMgr?.cancel(alarmIntent)
//
//    }
    fun isDaily(startTime: Long, endTime: Long): Boolean {
        return endTime - startTime >= 86400000
    }
    fun setLanguageEnglish(context: Context) {
        val locale = Locale("en")
        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)

        context.resources.updateConfiguration(config, context.resources.displayMetrics)
    }
    fun setLanguageArabic(context: Context) {
        val locale = Locale("ar")
        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)

        context.resources.updateConfiguration(config, context.resources.displayMetrics)
    }

    fun updateResources(context: Context, language: String): Boolean {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val resources: Resources = context.getResources()
        val configuration = resources.configuration
        configuration.locale = locale
        resources.updateConfiguration(configuration, resources.displayMetrics)

        return true
    }
    @SuppressLint("ObsoleteSdkInt")
    fun setAppLocale(localeCode: String, context: Context) {
        val resources = context.resources
        val dm = resources.displayMetrics
        val config: Configuration = resources.configuration
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            config.setLocale(Locale(localeCode.lowercase(Locale.getDefault())))
        } else {
            config.locale = Locale(localeCode.lowercase(Locale.getDefault()))
        }
        resources.updateConfiguration(config, dm)
    }

    @SuppressLint("ObsoleteSdkInt")
    fun changeLang(context: Context, lang_code: String): ContextWrapper? {
        var context: Context = context
        val sysLocale: Locale
        val rs: Resources = context.getResources()
        val config: Configuration = rs.getConfiguration()
        sysLocale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.getLocales().get(0)
        } else {
            config.locale
        }
        if (lang_code != "" && !sysLocale.getLanguage().equals(lang_code)) {
            val locale = Locale(lang_code)
            Locale.setDefault(locale)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                config.setLocale(locale)
            } else {
                config.locale = locale
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                context = context.createConfigurationContext(config)
            } else {
                context.getResources()
                    .updateConfiguration(config, context.getResources().getDisplayMetrics())
            }
        }
        return ContextWrapper(context)
    }
}