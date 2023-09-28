package com.example.weatherApp.apiResponseDataClasses

data class FullWeatherDataResponse(
    var list: List<HourlyWeatherInfoResponse> = listOf()
)