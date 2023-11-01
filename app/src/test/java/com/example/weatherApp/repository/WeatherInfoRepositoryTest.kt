package com.example.weatherApp.repository

import com.example.weatherApp.WeatherInfo
import com.example.weatherApp.WeatherInfoCallBack
import com.example.weatherApp.apiResponse.HourlyWeatherInfoResponse
import com.example.weatherApp.dao.WeatherInfoDao
import com.example.weatherApp.retrofit.WeatherForecastResponseService
import com.example.weatherApp.viewmodels.WeatherInfoViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import mockwebserver3.MockResponse
import mockwebserver3.MockWebServer
import org.junit.Assert.*

import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class WeatherInfoRepositoryTest {

    val dispatcher = TestCoroutineDispatcher()
    lateinit var repository: WeatherInfoRepository
    lateinit var mockwebserver: MockWebServer
   // lateinit var viewModel : WeatherInfoViewModel

    @Mock
    lateinit var callback: WeatherInfoCallBack

    @Mock
    lateinit var dao: WeatherInfoDao

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        mockwebserver = MockWebServer()
        MockitoAnnotations.openMocks(this)
        repository = WeatherInfoRepository(dao)
       // viewModel = WeatherInfoViewModel(repository)

    }

    @Test
    fun testFetchWeatherInfo() = runTest {

        val weatherInfoCallback = object : WeatherInfoCallBack {
            override fun onWeatherInfoFetched(weatherInfo: WeatherInfo) {
                Assert.assertEquals("Clear", weatherInfo.main)
                mockwebserver.takeRequest()
            }

            override fun onWeatherInfoFailure(error: String) {
                fail("onWeatherInfoFailure should not be called in this test")
                mockwebserver.takeRequest()
            }

            override fun onHourlyWeatherInfoFetched(hourlyWeatherInfo: List<HourlyWeatherInfoResponse>) {
                assertTrue(hourlyWeatherInfo.isNotEmpty())
                mockwebserver.takeRequest()
            }

            override fun onHourlyWeatherInfoFailure(error: String) {
                fail("onHourlyWeatherInfoFailure should not be called in this test")
                mockwebserver.takeRequest()
            }
        }


        repository.weatherInfoCallback = weatherInfoCallback


        val mockResponse = MockResponse()
        mockResponse.setBody("{}")
        mockResponse.setResponseCode(200)

        mockwebserver.enqueue(mockResponse)

         val data = WeatherInfo("", "",0.0)
         val result = repository.fetchWeatherInfo(0.0, 0.0)
       // val result = callback.onWeatherInfoFetched(data)
        //mockwebserver.takeRequest()



    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        mockwebserver.shutdown()
    }


}