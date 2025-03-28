package com.example.skycast.model.remote

import com.example.skycast.model.pojo.WeatherInfo
import com.example.skycast.model.pojo.WeatherResponse
import com.example.skycast.model.util.AppConstants
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {

    @GET("data/3.0/onecall")
    suspend  fun getCurrentWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String = "metric",
        @Query("lang") lang:String,
        @Query("appid") apiKey: String = AppConstants.WEATHER_API_KEY
    ): Response<WeatherResponse>

    @GET("data/2.5/weather")
    suspend fun getWeatherInfo(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String = AppConstants.WEATHER_API_KEY
    ): Response<WeatherInfo>

//    @GET("data/3.0/onecall/overview")
//    fun getOverviewWeather(
//        @Query("lat") lat: Double,
//        @Query("lon") lon: Double,
//        @Query("appid") apiKey: String
//    ): Response<WeatherResponse>
}
