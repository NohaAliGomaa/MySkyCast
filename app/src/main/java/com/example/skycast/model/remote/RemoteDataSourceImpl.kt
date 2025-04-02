package com.example.skycast.model.remote

import android.util.Log
import com.example.skycast.model.pojo.WeatherInfo
import com.example.skycast.model.pojo.WeatherResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class RemoteDataSourceImpl :IRemoteDataSource {
    private val WeatherApi: WeatherApiService = RetrofitClient.apiService

    override fun getCurrentWeather(lat: Double,
                                   lon: Double,
                                    lang:String,
                                   units: String
                                  ): Flow<WeatherResponse> = flow {
       val response = WeatherApi.getCurrentWeather(lat,lon,lang,units)
        Log.i("TAG", "Fetched ${response.body()?.current} products from API")
        if (response.isSuccessful && response.body() != null) {
            Log.i("TAG", "Successed ${response.body()?.current} products from API")
            response.body()?.let{ emit(it)}
        } else {
            throw Exception("API error: ${response.code()} - ${response.message()}")
        }
    }
    override fun getWeatherInfo(lat: Double, lon: Double, lang:String,
                                units: String): Flow<WeatherInfo> = flow {
        val response = WeatherApi.getWeatherInfo(lat, lon,lang,units)
        Log.d("WeatherRemote", "Raw response: ${response.raw()}")
        Log.d("WeatherRemote", "Error body: ${response.errorBody()?.string()}")

        if (response.isSuccessful && response.body() != null) {
            emit(response.body()!!)
        } else {
            throw Exception("API error: ${response.code()} - ${response.message()}")
        }
    }.flowOn(Dispatchers.IO)



}