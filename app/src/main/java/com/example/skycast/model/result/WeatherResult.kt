package com.example.skycast.model.result

import com.example.skycast.model.pojo.MyAlert
import com.example.skycast.model.pojo.WeatherInfo
import com.example.skycast.model.pojo.WeatherResponse

sealed class WeatherResult {
    data object Loading : WeatherResult()
    data class Success(val data: WeatherResponse) : WeatherResult()
    data class Failure(val error: Throwable) : WeatherResult()
}
//sealed class WeatherInfoState {
//    object Loading : WeatherInfoState()
//    data class Success(val weatherInfo: WeatherInfo) : WeatherInfoState()
//    data class Failure(val throwable: Throwable) : WeatherInfoState()
//}
sealed class  LocalDataState {
    class Success(var data: List<WeatherResponse>?):LocalDataState()
    class Fail(val msg : Throwable):LocalDataState()
    object Loading :LocalDataState()
}
sealed class  LocalDataStateAlerts {
    class Success(var data: List<MyAlert>?):LocalDataStateAlerts()
    class Fail(val msg : Throwable):LocalDataStateAlerts()
    object Loading :LocalDataStateAlerts()
}