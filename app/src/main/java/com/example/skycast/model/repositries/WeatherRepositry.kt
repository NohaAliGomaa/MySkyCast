package com.example.skycast.model.repositries

import com.example.skycast.model.pojo.WeatherInfo
import com.example.skycast.model.pojo.WeatherResponse
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