package com.example.weatherApp.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
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
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.whenever
import java.util.concurrent.CountDownLatch

class WeatherInfoViewModelTest {

    private val testDispatcher  = StandardTestDispatcher()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
     lateinit var weatherInfoObserver : Observer<WeatherInfo>

    @Mock
    lateinit var repository: WeatherInfoRepository

    lateinit var viewmodel: WeatherInfoViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        viewmodel = WeatherInfoViewModel(repository)
        viewmodel.weatherInfoLiveData.observeForever(weatherInfoObserver)
        Dispatchers.setMain(testDispatcher)
    }

    @Test
    fun testFetchWeatherInfo_Success(){
        val mockWeatherInfo = WeatherInfo("rain", "10d", 25.86)

        Mockito.`when`(repository.fetchWeatherInfo( 0.0,0.0,){p1, p2->
        }).then{
            val callback: (WeatherInfo?, String?) -> Unit = it.getArgument(2)
              callback(mockWeatherInfo, null)
        }
         viewmodel.fetchWeatherInfo(0.0,0.0)
         val result = viewmodel.weatherInfoLiveData.value
         viewmodel.weatherInfoLiveData.observeForever{
            Assert.assertEquals(mockWeatherInfo, result)
             Assert.assertEquals("rain", result?.main)
        }
    }

    @Test
    fun testFetchWeatherInfo_Error(){
        Mockito.`when`(repository.fetchWeatherInfo( 0.0,0.0,){p1, p2->
        }).thenAnswer{
            val callback: (WeatherInfo?, String?) -> Unit = it.getArgument(2)
            callback(null, "no data found")
        }
        viewmodel.fetchWeatherInfo(0.0,0.0)
        val result = viewmodel.weatherInfoLiveData.value
        viewmodel.weatherInfoLiveData.observeForever{
            Assert.assertNull(null, result)
            Assert.assertEquals("no data found", it)
        }
    }

    @Test
    fun testFetchHourlyWeatherInfo_Success(){
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

        Mockito.`when`(repository.fetchWeatherInfoHourly(0.0, 0.0){p1, p2->}).thenAnswer {
            val callback: (List<HourlyWeatherInfoResponse>?, String?) -> Unit = it.getArgument(2)
            callback(mockHourlyWeatherInfo, null)
        }

        viewmodel.fetchWeatherInfoHourly(0.0, 0.0)
        val result = viewmodel.listOfWeatherInfoLiveData.value
        viewmodel.listOfWeatherInfoLiveData.observeForever{
            Assert.assertEquals(mockHourlyWeatherInfo, result)
        }

    }

    @Test
    fun testFetchHourlyWeatherInfo_Error(){
        val mockHourlyWeatherInfo = listOf(
            HourlyWeatherInfoResponse(
                TemperatureValueResponse(0.0, 0.0, 0.0),
                listOf(WeatherResponse("", "")),
                WindResponse(0.0),
                "0-0-0 00:00:00"
            )
        )

        Mockito.`when`(repository.fetchWeatherInfoHourly(0.0, 0.0){p1, p2->}).thenAnswer {
            val callback: (List<HourlyWeatherInfoResponse>?, String?) -> Unit = it.getArgument(2)
            callback(null, "no data found")
        }

        viewmodel.fetchWeatherInfoHourly(0.0, 0.0)
        val result = viewmodel.listOfWeatherInfoLiveData.value
        viewmodel.listOfWeatherInfoLiveData.observeForever{
            Assert.assertNull(result)
            Assert.assertEquals("no data found", it)
        }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
}