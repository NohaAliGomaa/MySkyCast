package com.example.skycast.model.repositries

import com.example.skycast.model.local.ILocalDataSource
import com.example.skycast.model.local.LocalDataSource
import com.example.skycast.model.pojo.WeatherInfo
import com.example.skycast.model.pojo.WeatherResponse
import com.example.skycast.model.remote.IRemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class WeatherRepositry(
    private val remoteDataSource: IRemoteDataSource,
    private val localDataSource: ILocalDataSource
) :IRepositry{


    override suspend fun getCurrentWeather(lat: Double,
                                           lon: Double,
                                           lang:String,
                                           units: String,
                                           isOnline : Boolean) : Flow<WeatherResponse>{
        return if (isOnline) {
            remoteDataSource.getCurrentWeather(lat, lon, units)
        } else {
            getCurrentWeathers() ?: flow { } // Return empty flow if null (optional safeguard)
        }
    }

    override suspend fun getWeatherInfo(lat: Double, lon: Double): Flow<WeatherInfo> {
       return  remoteDataSource.getWeatherInfo(lat,lon)
    }

    override suspend fun getFavoriteWeathers(): Flow<List<WeatherResponse>?> {
        return  localDataSource.getFavoriteWeathers()
    }

    override suspend fun getCurrentWeathers(): Flow<WeatherResponse>? {
        return localDataSource.getCurrentWeathers()
    }

    override  suspend fun insertWeather(weather: WeatherResponse): Long {
        return localDataSource.insertWeather(weather)
    }

    override suspend fun insertCurrentWeather(weather: WeatherResponse): Long {
        return localDataSource.insertCurrentWeather(weather)
    }
}