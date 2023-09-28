package com.example.weatherApp.apiResponseDataClasses

data class HourlyWeatherInfoResponse(
    val main: TemperatureValueResponse,
    val weather: List<WeatherResponse>,
    val wind: WindResponse,
    val dt_txt: String
)
