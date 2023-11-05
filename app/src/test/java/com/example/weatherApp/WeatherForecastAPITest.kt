package com.example.weatherApp

import com.example.weatherApp.retrofit.WeatherForecastResponseService
import kotlinx.coroutines.test.runTest
import mockwebserver3.MockResponse
import mockwebserver3.MockWebServer
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class WeatherForecastAPITest {

    lateinit var mockWebServer: MockWebServer
    lateinit var weatherForecastApi : WeatherForecastResponseService

    @Before
    fun setup(){
        mockWebServer = MockWebServer()
        weatherForecastApi = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory((GsonConverterFactory.create()))
            .build().create(WeatherForecastResponseService::class.java)
    }

    @Test
    fun testWeatherForecast_Success() = runTest{
     val mockResponse = MockResponse()
         mockResponse.setBody("{}")
         mockResponse.setResponseCode(200)
         mockWebServer.enqueue(mockResponse)

        val response = weatherForecastApi.getWeatherForecast(0.0, 0.0, ConstantKeys.APP_ID)
        mockWebServer.takeRequest()

        Assert.assertEquals(true, response.body()!!.list.isEmpty())
        Assert.assertEquals(0, response.body()!!.list.size)
    }

    @Test
    fun testWeatherForecast_Error() = runTest{
        val mockResponse = MockResponse()
        mockResponse.setBody("Something went wrong")
        mockResponse.setResponseCode(404)
        mockWebServer.enqueue(mockResponse)

        val response = weatherForecastApi.getWeatherForecast(0.0, 0.0, ConstantKeys.APP_ID)
        mockWebServer.takeRequest()

        Assert.assertEquals(false, response.isSuccessful)
        Assert.assertEquals(404, response.code())
    }

    @After
    fun tearDown(){
     mockWebServer.shutdown()
    }
}