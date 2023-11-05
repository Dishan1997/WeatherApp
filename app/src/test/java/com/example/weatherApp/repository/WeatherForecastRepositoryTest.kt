package com.example.weatherApp.repository

import com.example.weatherApp.ConstantKeys
import com.example.weatherApp.apiResponse.FullWeatherDataResponse
import com.example.weatherApp.apiResponse.HourlyWeatherInfoResponse
import com.example.weatherApp.apiResponse.TemperatureValueResponse
import com.example.weatherApp.apiResponse.WeatherResponse
import com.example.weatherApp.apiResponse.WindResponse
import com.example.weatherApp.dao.WeatherForecastDao
import com.example.weatherApp.retrofit.WeatherForecastResponseService
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody
import org.junit.Assert
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
    fun testWeatherForecastRepository_Success() = runTest {
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

        Mockito.`when`(
            weatherForecastResponseService.getWeatherForecast(
                0.0,
                0.0,
                ConstantKeys.APP_ID
            )
        )
            .thenAnswer { Response.success(response) }

        val result = repository.fetchWeatherForecast(0.0, 0.0)
        Assert.assertEquals(1, result!!.size)
    }

    @Test
    fun testWeatherForecastRepository_expectError() = runTest {
        val response =
            Response.error<FullWeatherDataResponse>(404, ResponseBody.create(null, "not found"))
        Mockito.`when`(
            weatherForecastResponseService.getWeatherForecast(
                0.0,
                0.0,
                ConstantKeys.APP_ID
            )
        )
            .thenAnswer { response }

        val result = repository.fetchWeatherForecast(0.0, 0.0)
        Assert.assertEquals(0, result?.size)
        Assert.assertEquals("not found", response.errorBody()?.string())

    }

    @Test
    fun testGetWeatherInfoData_Success() = runTest {
        val mockData = listOf(
            HourlyWeatherInfoResponse(
                TemperatureValueResponse(0.0, 0.0, 0.0),
                listOf(WeatherResponse("", "")),
                WindResponse(0.0),
                ""
            )
        )
    Mockito.`when`(weatherForecastDao.getWeatherData()).thenReturn(mockData)
        val result = repository.getRealmData()
        Assert.assertEquals(mockData, result)
       Assert.assertEquals(1, result?.size)
    }
    @Test
    fun testGetWeatherInfoData_Error() = runTest {
        Mockito.`when`(weatherForecastDao.getWeatherData()).thenReturn(emptyList())
        val result = repository.getRealmData()
        Assert.assertEquals(0, result?.size)
    }


}