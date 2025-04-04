package com.example.skycast.model.repositries

import com.example.skycast.model.local.ILocalDataSource
import com.example.skycast.model.local.LocalDataSource
import com.example.skycast.model.pojo.AlertSettings
import com.example.skycast.model.pojo.MyAlert
import com.example.skycast.model.pojo.Settings
import com.example.skycast.model.pojo.WeatherInfo
import com.example.skycast.model.pojo.WeatherResponse
import com.example.skycast.model.remote.IRemoteDataSource
import com.example.skycast.model.sharedpreferences.SharedManager
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
        return remoteDataSource.getCurrentWeather(lat, lon,lang,units)

    }

    override suspend fun getWeatherInfo(lat: Double, lon: Double,  lang: String,
                                        units: String): Flow<WeatherInfo> {
       return  remoteDataSource.getWeatherInfo(lat,lon,lang,units)
    }

    override suspend fun getFavoriteWeathers(): Flow<List<WeatherResponse>?> {
        return  localDataSource.getFavoriteWeathers()
    }

    override  fun getCurrentWeathers(): Flow<List<WeatherResponse>?> {
        return localDataSource.getCurrentWeathers()
    }

    override  suspend fun insertWeather(weather: WeatherResponse): Long {
        return localDataSource.insertWeather(weather)
    }

    override suspend fun insertCurrentWeather(weather: WeatherResponse): Long {
        return localDataSource.insertCurrentWeather(weather)
    }

    override suspend fun insertOrUpdateCurrentWeather(weather: WeatherResponse) {
        return localDataSource.insertOrUpdateCurrentWeather(weather)
    }

    override suspend fun deleteFavorite(weather: WeatherResponse): Int {
        return localDataSource.deleteFavorite((weather))
    }

    //shared
    override fun saveSettings(settings: Settings){
        // SharedManger.init(context)
        SharedManager.saveSettings(settings)
    }
    override fun getSettings():Settings?{
        // SharedManger.init(context)
        return SharedManager.getSettings()
    }
    //alert room
    override suspend fun insertAlert(alert: MyAlert)=localDataSource.insertAlert(alert)
    override suspend fun deleteAlert(alert: MyAlert)=localDataSource.deleteAlert(alert)
    override fun getAlerts()=localDataSource.getAlerts()
    override fun getAlert(id: Long)=localDataSource.getAlert(id!!)
    override fun saveAlertSettings(alertSettings: AlertSettings) {
        // SharedManger.init(context)
        SharedManager.saveAlertSettings(alertSettings)
    }
    override fun getAlertSettings(): AlertSettings?{
        // SharedManger.init(context)
        return SharedManager.getAlertSettings()
    }
}