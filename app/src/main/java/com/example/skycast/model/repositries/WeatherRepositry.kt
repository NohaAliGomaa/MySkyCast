package com.example.skycast.model.repositries

import android.util.Log
import com.example.skycast.model.models.WeatherInfo
import com.example.skycast.model.models.WeatherResponse
import com.example.skycast.model.remote.WeatherRemoteDataSource
import kotlinx.coroutines.flow.Flow

class WeatherRepositry(private val remoteDataSource: WeatherRemoteDataSource) :WeatherRepositryInterface{
    override fun getCurrentWeather(lat: Double,
                                   lon: Double,
                                    lang:String,
                                   units: String) : Flow<WeatherResponse>{
        return remoteDataSource.getCurrentWeather(lat,lon,units)
    }

    override fun getWeatherInfo(lat: Double, lon: Double): Flow<WeatherInfo> {
       return  remoteDataSource.getWeatherInfo(lat,lon)
    }
}