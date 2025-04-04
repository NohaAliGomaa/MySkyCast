package com.example.skycast.model.local

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.skycast.model.local.LocalDataSource
import com.example.skycast.model.local.WeatherDataBse
import com.example.skycast.model.pojo.WeatherResponse
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.*
import org.junit.Assert.assertEquals
import org.junit.runner.RunWith



@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class LocalDataSourceTest {

    private lateinit var db: WeatherDataBse
    private lateinit var localDataSource: LocalDataSource

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, WeatherDataBse::class.java)
            .allowMainThreadQueries()
            .build()

        localDataSource = LocalDataSource(context).apply {
            // Inject in-memory database for testing
            room = db
        }
    }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun insertOrUpdateCurrentWeather_replacesPreviousWeather() = runTest {
        val weather1 = WeatherResponse(10.0,20.0, isFavorite = false, name = "London")
        val weather2 = WeatherResponse(10.0,20.0, isFavorite = false, name = "Paris")

        localDataSource.insertCurrentWeather(weather1)
        localDataSource.insertOrUpdateCurrentWeather(weather2)

        val result = localDataSource.getCurrentWeathers().first()

        assertEquals(1, result?.size)
        assertEquals("Paris", result?.first()?.name)
        assertEquals(false , result?.first()?.isFavorite)
    }

    @Test
    fun getFavoriteWeathers_returnsOnlyFavoriteItems() = runTest {
        val weather1 = WeatherResponse(10.0,20.0, isFavorite = true, name = "New York")
        val weather2 = WeatherResponse(10.0,20.0, isFavorite = false, name = "Tokyo" )

        localDataSource.insertWeather(weather1)
        localDataSource.insertWeather(weather2)

        val favorites = localDataSource.getFavoriteWeathers().first()

        assertEquals(1, favorites?.size)
        assertEquals("New York", favorites?.first()?.name)
        assertEquals(true , favorites?.first()?.isFavorite)
    }
}
