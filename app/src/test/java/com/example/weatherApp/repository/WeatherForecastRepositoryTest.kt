package com.example.weatherApp.repository

import com.example.weatherApp.ConstantKeys
import com.example.weatherApp.apiResponse.FullWeatherDataResponse
import com.example.weatherApp.apiResponse.HourlyWeatherInfoResponse
import com.example.weatherApp.apiResponse.TemperatureValueResponse
import com.example.weatherApp.apiResponse.WeatherResponse
import com.example.weatherApp.apiResponse.WindResponse
import com.example.weatherApp.dao.WeatherForecastDao
import com.example.weatherApp.realm.WeatherForecast
import com.example.weatherApp.retrofit.WeatherForecastResponseService
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Assert.*

import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import retrofit2.Response

class WeatherForecastRepositoryTest {
    private lateinit var repository: WeatherForecastRepository

    @Mock
    lateinit var weatherForecastResponseService: WeatherForecastResponseService

    @Mock
    lateinit var weatherForecastDao: WeatherForecastDao


    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        repository = WeatherForecastRepository(weatherForecastResponseService, weatherForecastDao)
    }

    @Test
    fun testWeatherForecastRepository_expectSuccess() = runTest {
        val response =
            FullWeatherDataResponse(
                listOf(
                    HourlyWeatherInfoResponse(
                        TemperatureValueResponse(0.0, 0.0, 0.0),
                        listOf(WeatherResponse("", "")),
                        WindResponse(0.0),
                        ""
                    )
                )
            )

        Mockito.`when`(weatherForecastResponseService.getWeatherForecast(0.0, 0.0, ConstantKeys.APP_ID))
            .thenAnswer { Response.success(response) }

        val result = repository.fetchWeatherForecast(0.0, 0.0)
        Assert.assertEquals(1, result!!.size)
    }

    @Test
    fun testWeatherForecastRepository_expectError() = runTest {
        val response =
            FullWeatherDataResponse(
                listOf(
                    HourlyWeatherInfoResponse(
                        TemperatureValueResponse(0.0, 0.0, 0.0),
                        listOf(WeatherResponse("", "")),
                        WindResponse(0.0),
                        ""
                    )
                )
            )

//        Mockito.`when`(weatherForecastResponseService.getWeatherForecast(0.0, 0.0, ConstantKeys.APP_ID))
//            .thenAnswer { Response }

        val result = repository.fetchWeatherForecast(0.0, 0.0)
        Assert.assertEquals(1, result!!.size)
    }

}