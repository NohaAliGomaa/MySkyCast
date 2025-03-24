package com.example.skycast.model.repositries

import com.example.skycast.model.models.WeatherResponse
import com.example.skycast.model.remote.WeatherRemoteDataSource
import kotlinx.coroutines.flow.Flow

class WeatherRepositry(private val remoteDataSource: WeatherRemoteDataSource) :WeatherRepositryInterface{
    override fun getCurrentWeather(lat: Double,
                                   lon: Double,
                                   units: String) : Flow<WeatherResponse>{
        return remoteDataSource.getCurrentWeather(lat,lon,units)
    }
}