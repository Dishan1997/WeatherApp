package com.example.weatherApp.repository

import com.example.weatherApp.ConstantKeys
import com.example.weatherApp.apiResponse.HourlyWeatherInfoResponse
import com.example.weatherApp.dao.WeatherForecastDao
import com.example.weatherApp.retrofit.WeatherForecastResponseService

class WeatherForecastRepository(private val temperatureServices: WeatherForecastResponseService, private val dao : WeatherForecastDao) {

    suspend fun fetchWeatherForecast(lat: Double, lon: Double): List<HourlyWeatherInfoResponse>? {
        val response = temperatureServices.getWeatherForecast(lat, lon, ConstantKeys.APP_ID)
        if (response.isSuccessful) {
            val data = response.body()?.list
            data?.let {
                dao.saveWeatherData(it)
            }
            return data
        } else {
            return emptyList()
        }
    }

    fun getRealmData() : List<HourlyWeatherInfoResponse>?{
        val realmData = dao.getWeatherData()
        return realmData
    }

}