package com.example.weatherApp.repository

import com.example.weatherApp.ConstantKeys
import com.example.weatherApp.apiResponse.FullWeatherDataResponse
import com.example.weatherApp.apiResponse.HourlyWeatherInfoResponse
import com.example.weatherApp.dao.WeatherForecastDao
import com.example.weatherApp.retrofit.WeatherForecastResponseService
import retrofit2.Response

class WeatherForecastRepository(private val temperatureServices: WeatherForecastResponseService, private val dao : WeatherForecastDao) {

    suspend fun fetchWeatherForecast(lat: Double, lon: Double): List<HourlyWeatherInfoResponse>? {
        val response = temperatureServices.getWeatherForecast(lat, lon, ConstantKeys.APP_ID)
        if (response.isSuccessful) {
            val data = response.body()?.list ?: emptyList()
            data?.let {
                dao.setDataToRealm(it)
            }
            return data
        } else {
            throw error("no data found")
        }
    }

    fun getRealmData() : List<HourlyWeatherInfoResponse>?{
        val realmData = dao.getDataFromRealm()
        return realmData
    }

    fun closeRealm() {
        dao.closeRealm()
    }
}