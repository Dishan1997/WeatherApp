package com.example.weatherApp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherApp.ConstantKeys
import com.example.weatherApp.WeatherForecastInfo
import com.example.weatherApp.apiResponse.HourlyWeatherInfoResponse
import com.example.weatherApp.repository.WeatherForecastRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okio.IOException
import java.text.SimpleDateFormat


class WeatherDataForecastViewModel(private val repository: WeatherForecastRepository) :
    ViewModel() {

    private var weatherData = MutableLiveData<List<HourlyWeatherInfoResponse>>()
    val weatherLiveData: LiveData<List<HourlyWeatherInfoResponse>> = weatherData

    private var weatherInfo: MutableLiveData<WeatherForecastInfo> = MutableLiveData()
    val weatherInfoLiveData: LiveData<WeatherForecastInfo>
        get() = weatherInfo

    suspend fun getWeatherForecast(lat: Double, lon: Double) {
        viewModelScope.launch {
            val weatherForecast = repository.fetchWeatherForecast(lat, lon)
            if (weatherForecast==null) {
                val realmData = repository.getRealmData()
                weatherData.value = realmData!!
                displayWeatherInfo(realmData)
            } else {
                weatherData.value = weatherForecast!!
                displayWeatherInfo(weatherForecast)
            }
        }
    }

    fun displayWeatherInfo(weatherResponse: List<HourlyWeatherInfoResponse>) {

        if (weatherResponse.isNotEmpty()) {
            val temperatureValue = weatherResponse[0].main.temp - 273.15
            val temperature = temperatureValue.toInt()

            val type = weatherResponse[0].weather[0].main

            val inputDate = weatherResponse[0].dt_txt
            val inputFormat = SimpleDateFormat(ConstantKeys.DATE_PATTERN)
            val outputFormat = SimpleDateFormat(ConstantKeys.DAY_PATTERN)
            val outputDate = inputFormat.parse(inputDate)
            val date = outputFormat.format(outputDate)

            val minTemperatureValue = weatherResponse[0].main.temp_min - 273.15
            val minTemperature = minTemperatureValue.toInt()

            val maxTemperatureValue = weatherResponse[0].main.temp_max - 273.15
            val maxTemperature = maxTemperatureValue.toInt()
            val icon = weatherResponse[0].weather[0].icon

            viewModelScope.launch(Dispatchers.Main) {
                weatherInfo.value = WeatherForecastInfo(
                    temperature,
                    type,
                    date,
                    minTemperature,
                    maxTemperature,
                    icon
                )
            }
        }
        else{
            throw IOException("no data found")
        }
    }

}