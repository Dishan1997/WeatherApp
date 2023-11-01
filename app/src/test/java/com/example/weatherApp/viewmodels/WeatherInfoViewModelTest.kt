package com.example.weatherApp.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.weatherApp.WeatherInfo
import com.example.weatherApp.apiResponse.HourlyWeatherInfoResponse
import com.example.weatherApp.apiResponse.TemperatureValueResponse
import com.example.weatherApp.apiResponse.WeatherResponse
import com.example.weatherApp.apiResponse.WindResponse
import com.example.weatherApp.getOrAwaitValue
import com.example.weatherApp.repository.WeatherInfoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

class WeatherInfoViewModelTest {

    private val testDispatcher  = StandardTestDispatcher()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    lateinit var repository: WeatherInfoRepository

    lateinit var viewmodel: WeatherInfoViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        viewmodel = WeatherInfoViewModel(repository)

        Dispatchers.setMain(testDispatcher)
    }

    @Test
    fun testFetchWeatherInfoSuccess() = runTest{
        val mockWeatherInfo = WeatherInfo("rain", "10d", 25.86)
        val mock2 = WeatherInfo("rainy", "1d", 25.76)
        val list = listOf<WeatherInfo>(
            mockWeatherInfo,
            mock2
        )

        Mockito.`when`(repository.weatherInfoCallback).thenReturn(viewmodel)
        Mockito.`when`(repository.fetchWeatherInfo( 0.0,0.0)).then {
            repository.weatherInfoCallback?.onWeatherInfoFetched(mockWeatherInfo)
        }

         viewmodel.fetchWeatherInfo(0.0,0.0)
         testDispatcher.scheduler.advanceUntilIdle()
         val result = viewmodel.weatherInfoLiveData.getOrAwaitValue()
         Assert.assertEquals(mockWeatherInfo, result)
         Assert.assertEquals(2, list.size)

    }

    @Test
    fun testFetchHourlyWeatherInfo() = runTest{
        val mockHourlyWeatherInfo = listOf(
            HourlyWeatherInfoResponse(
                TemperatureValueResponse(23.5, 22.0, 21.0),
                listOf(WeatherResponse("main", "icon")),
                WindResponse(5.0),
                "2023-10-25 12:00:00"
            ),
            HourlyWeatherInfoResponse(
                TemperatureValueResponse(24.5, 23.0, 22.0),
                listOf(WeatherResponse("main2", "icon2")),
                WindResponse(6.0),
                "2023-10-25 1:00:00"
            )
        )

        Mockito.`when`(repository.weatherInfoCallback).thenReturn(viewmodel)
        Mockito.`when`(repository.fetchWeatherInfoHourly(0.0, 0.0)).thenAnswer {
            repository.weatherInfoCallback?.onHourlyWeatherInfoFetched(mockHourlyWeatherInfo)
        }

        viewmodel.fetchWeatherInfoHourly(0.0, 0.0)
        testDispatcher.scheduler.advanceUntilIdle()
        val result = viewmodel.listOfWeatherInfoLiveData.value
        Assert.assertEquals(mockHourlyWeatherInfo, result)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
}