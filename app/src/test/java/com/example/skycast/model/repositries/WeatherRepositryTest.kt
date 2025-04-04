package com.example.skycast.model.repositries

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import com.example.skycast.model.local.ILocalDataSource
import com.example.skycast.model.pojo.WeatherResponse
import com.example.skycast.model.remote.IRemoteDataSource
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*


@OptIn(ExperimentalCoroutinesApi::class)
class WeatherRepositryTest {

    private lateinit var remoteDataSource: IRemoteDataSource
    private lateinit var localDataSource: ILocalDataSource
    private lateinit var repository: WeatherRepositry

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        remoteDataSource = mock(IRemoteDataSource::class.java)
        localDataSource = mock(ILocalDataSource::class.java)
        repository = WeatherRepositry(remoteDataSource, localDataSource)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getCurrentWeather returns expected data`() = runTest {
        // Given
        val expectedWeather = WeatherResponse(10.0,20.0, current = mockk(relaxed = true))
        `when`(remoteDataSource.getCurrentWeather(10.0, 20.0, "en", "metric"))
            .thenReturn(flow { emit(expectedWeather) })

        // When
        val resultFlow = repository.getCurrentWeather(10.0, 20.0, "en", "metric", true)

        // Then
        resultFlow.collect { result ->
            assertEquals(expectedWeather, result)
        }
    }

    @Test
    fun `insertWeather inserts data and returns id`() = runTest {
        // Given
        val weather = WeatherResponse(10.0,20.0, current = mockk(relaxed = true))
        `when`(localDataSource.insertWeather(weather)).thenReturn(1L)

        // When
        val result = repository.insertWeather(weather)

        // Then
        assertEquals(1L, result)
    }
}
