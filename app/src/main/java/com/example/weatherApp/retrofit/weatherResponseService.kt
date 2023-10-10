package com.example.weatherApp.retrofit

import com.example.weatherApp.apiResponse.FullWeatherDataResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface weatherResponseService {
    @GET("data/2.5/forecast")
    fun getWeatherForecast(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") appid: String
    ): Call<FullWeatherDataResponse>
}