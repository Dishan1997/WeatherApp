package com.example.weatherApp.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.weatherApp.apiResponse.HourlyWeatherInfoResponse
import com.example.weatherApp.apiResponse.TemperatureValueResponse
import com.example.weatherApp.apiResponse.WeatherResponse
import com.example.weatherApp.apiResponse.WindResponse
import com.example.weatherApp.repository.WeatherForecastRepository
import com.example.weatherApp.repository.WeatherInfoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.*

import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

class WeatherDataForecastViewModelTest {
    private val testDispatcher  = StandardTestDispatcher()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    lateinit var repository: WeatherForecastRepository

    lateinit var viewmodel: WeatherDataForecastViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        viewmodel = WeatherDataForecastViewModel(repository)
        Dispatchers.setMain(testDispatcher)
    }

    @Test
    fun testWeatherForecast() = runTest{
        val mockData = listOf(
            HourlyWeatherInfoResponse(
                TemperatureValueResponse(0.0, 0.0, 0.0),
                listOf( WeatherResponse("", "")),
                WindResponse(0.0),
                "2023-10-25 1:00:00"
            ),
            HourlyWeatherInfoResponse(
                TemperatureValueResponse(0.1, 0.2, 0.3),
                listOf( WeatherResponse("a", "b")),
                WindResponse(0.4),
                "2023-11-25 2:00:00"
            )

        )
       Mockito.`when`(repository.fetchWeatherForecast(0.0, 0.0)).thenReturn(mockData)

        viewmodel.getWeatherForecast(0.0,0.0)
        testDispatcher.scheduler.advanceUntilIdle()
        val result = viewmodel.weatherLiveData.value
        Assert.assertEquals(mockData, result)
        Assert.assertEquals(2, result?.size)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
}