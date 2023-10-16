package com.example.weatherApp

import com.example.weatherApp.apiResponse.HourlyWeatherInfoResponse

interface WeatherInfoCallBack {
    fun onWeatherInfoFetched(weatherInfo : WeatherInfo)
    fun onHourlyWeatherInfoFetched(hourlyWeatherInfo : List<HourlyWeatherInfoResponse>)
    fun onWeatherInfoFailure(errorResult: String)
    fun onHourlyWeatherInfoFailure(errorResult: String)
}