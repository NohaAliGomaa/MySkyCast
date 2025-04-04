package com.example.skycast.model.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.skycast.model.pojo.WeatherResponse
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class WeatherDaoTest {

    private lateinit var database: WeatherDataBse
    private lateinit var weatherDao: WeatherDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            WeatherDataBse::class.java
        ).allowMainThreadQueries().build()

        weatherDao = database.getWeatherDao()
    }

    @After
    fun teardown() {
        database.close()
    }
    @Test
    fun getFavoriteWeathers_returnsOnlyFavorites() = runTest {
        val weather1 = WeatherResponse(10.0,20.0, isFavorite = true, name = "New York")
        val weather2 = WeatherResponse(10.0,20.0, isFavorite = false, name = "Tokyo" )

        weatherDao.insetWeather(weather1)
        weatherDao.insetWeather(weather2)

        val favorites = weatherDao.getFavoriteWeathers().first()

        assertEquals(1, favorites?.size)
        assertEquals("New York", favorites?.first()?.name)
    }
    @Test
    fun insertOrUpdateCurrentWeather_replacesExistingCurrentWeather() = runTest {
        val weather1 = WeatherResponse(10.0,20.0, isFavorite = false, name = "London")
        val weather2 = WeatherResponse(10.0,20.0, isFavorite = false, name = "Paris")

        weatherDao.insertCurrentWeather(weather1)

        weatherDao.insertOrUpdateCurrentWeather(weather2)

        val currentWeathers = weatherDao.getCurrentWeathers().first()

        assertEquals(1, currentWeathers?.size)
        assertEquals("Paris", currentWeathers?.first()?.name)
    }


}
