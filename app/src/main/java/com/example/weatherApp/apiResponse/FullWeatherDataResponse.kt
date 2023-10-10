package com.example.weatherApp.apiResponse

data class FullWeatherDataResponse(
    var list: List<HourlyWeatherInfoResponse> = listOf()
)