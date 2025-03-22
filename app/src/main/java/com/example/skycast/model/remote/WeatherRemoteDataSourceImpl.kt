package com.example.skycast.model.remote

import android.util.Log
import com.example.skycast.model.models.WeatherResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class WeatherRemoteDataSourceImpl :WeatherRemoteDataSource {
    private val WeatherApi: WeatherApiService = RetrofitClient.apiService

    override fun getCurrentWeather(): Flow<WeatherResponse> = flow {
       val response = WeatherApi.getCurrentWeather(35.596048,-5.344827,"85e90ac76ec05044a030f7ee6cdf4591")
        Log.i("TAG", "Fetched ${response.body()?.current} products from API")
        if (response.isSuccessful && response.body() != null) {
            Log.i("TAG", "Successed ${response.body()?.current} products from API")
            response.body()?.let{ emit(it)}
        } else {
            throw Exception("API error: ${response.code()} - ${response.message()}")
        }
    }
}