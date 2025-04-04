package com.example.skycast.viewmodel

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.skycast.MainDispatcherRule
import com.example.skycast.model.pojo.WeatherInfo
import com.example.skycast.model.pojo.WeatherResponse
import com.example.skycast.model.repositries.WeatherRepositry
import com.example.skycast.model.result.LocalDataState
import com.example.skycast.model.result.WeatherResult
import com.example.skycast.model.util.NetworkUtils
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config


@RunWith(AndroidJUnit4::class)
@Config(sdk = [28])
class WeatherViewModelTest {
    private lateinit var viewModel: WeatherViewModel
    private lateinit var repo: WeatherRepositry
    private lateinit var context: Context

    // Uncomment this if you need to set the main dispatcher
    // @get:Rule
    // val coroutineRule = MainDispatcherRule(UnconfinedTestDispatcher())

    @Before
    fun setup() {
        repo = mockk(relaxed = true)
        context = mockk()
        viewModel = WeatherViewModel(repo, context)
    }

    @Test
    fun getCurrentWeather_Returns_Success_When_Online_And_Data_Is_Valid() = runTest {
        // Arrange
        val lat = 30.0
        val lon = 31.0
        val lang = "en"
        val units = "metric"
        val mockWeather = WeatherResponse(lat, lon, current = mockk(relaxed = true))
        val mockInfo = WeatherInfo(name = "Cairo")
        mockkObject(NetworkUtils)
        every { NetworkUtils.isInternetAvailable(any()) } returns true


        every { NetworkUtils.isInternetAvailable(context) } returns true
        coEvery { repo.getCurrentWeather(lat, lon, lang, units, true) } returns flow {
            emit(mockWeather)
        }
        coEvery { repo.getWeatherInfo(lat, lon, lang, units) } returns flow {
            emit(mockInfo)
        }
        coEvery { repo.insertOrUpdateCurrentWeather(any()) } just Runs

        // When
        viewModel.getCurrentWeather(lat, lon, lang, units)
        // Assert
        val result = viewModel.weather.value
        assertTrue(result is WeatherResult.Success)
        assertEquals(mockWeather.current, (result as WeatherResult.Success).data.current)
    }

    @Test
    fun `insertFavorite adds weather to favorites when online`() = runTest {
        // Arrange
        val lat = 30.0
        val lon = 31.0
        val lang = "en"
        val units = "metric"
        val mockWeather = WeatherResponse(lat,lon,current = mockk(relaxed = true))
        val mockInfo = WeatherInfo(name = "Alexandria", sys = mockk { every { sunset } returns 123; every { sunrise } returns 456 })
        mockkObject(NetworkUtils)
        every { NetworkUtils.isInternetAvailable(any()) } returns true

        every { NetworkUtils.isInternetAvailable(context) } returns true
        coEvery { repo.getCurrentWeather(lat, lon, lang, units, true) } returns flow { emit(mockWeather) }
        coEvery { repo.getWeatherInfo(lat, lon, lang, units) } returns flow { emit(mockInfo) }
        coEvery { repo.insertOrUpdateCurrentWeather(any()) } just Runs

        // Act
        viewModel.insertFavorite(lat, lon, lang, units)

        // Assert
        val result = viewModel.favWeather.value
        assertTrue(result is LocalDataState.Success)
        assertEquals((result as LocalDataState.Success).data?.first()?.isFavorite, true)
    }

}