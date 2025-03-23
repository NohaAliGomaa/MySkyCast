package com.example.skycast.model.remote

import com.example.skycast.model.models.WeatherResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {

    @GET("data/3.0/onecall")
    suspend  fun getCurrentWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String = "metric",
        @Query("appid") apiKey: String
    ): Response<WeatherResponse>

    @GET("data/3.0/onecall/timemachine")
    fun getHistoricalWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("dt") timestamp: Long,
        @Query("appid") apiKey: String
    ): Response<WeatherResponse>

    @GET("data/3.0/onecall/overview")
    fun getOverviewWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String
    ): Response<WeatherResponse>
}
