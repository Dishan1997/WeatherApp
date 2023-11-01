package com.example.weatherApp.retrofit

import com.example.weatherApp.apiResponse.FullWeatherDataResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherForecastResponseService {
    @GET("data/2.5/forecast")
    suspend fun getWeatherForecast(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") appid: String
    ): Response<FullWeatherDataResponse>

    companion object{
        private var instance : WeatherForecastResponseService? = null
        fun getInstance(): WeatherForecastResponseService{
            if(instance == null){
                instance = RetrofitInstance.getRetrofitInstance().create(WeatherForecastResponseService::class.java)
            }
            return instance!!
        }
    }
}