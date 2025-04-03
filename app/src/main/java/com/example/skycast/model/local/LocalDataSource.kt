package com.example.skycast.model.local

import android.content.Context
import androidx.core.graphics.rotationMatrix
import com.example.skycast.model.pojo.WeatherResponse
import kotlinx.coroutines.flow.Flow

class LocalDataSource(var context: Context) : ILocalDataSource  {
    var room:WeatherDataBse =WeatherDataBse.getInstance(context)
    init {
        room=WeatherDataBse.getInstance(context)
    }
    override fun getFavoriteWeathers(): Flow<List<WeatherResponse>?> {
      return room.getWeatherDao().getFavoriteWeathers()
    }

    override suspend fun insertWeather(weather: WeatherResponse): Long {
       return  room.getWeatherDao().insetWeather(weather)
    }

    override suspend fun deleteFavorite(weather: WeatherResponse): Int {
       return  room.getWeatherDao().deleteFavorite(weather)
    }

    override suspend fun deleteCurrent(): Int {
       return room.getWeatherDao().deleteCurrent()
    }

    override suspend fun insertCurrentWeather(weather: WeatherResponse): Long {
        return  room.getWeatherDao().insertCurrentWeather(weather)
    }

    override fun getCurrentWeathers(): Flow<List<WeatherResponse>?> {
       return  room.getWeatherDao().getCurrentWeathers()
    }

    override suspend fun insertOrUpdateCurrentWeather(weather: WeatherResponse) {
        return room.getWeatherDao().insertOrUpdateCurrentWeather(weather)
    }

    override suspend fun updateFavWeather(weather: WeatherResponse) {
     return room.getWeatherDao().updateFavWeather(weather)
    }
}