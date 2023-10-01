package com.example.weatherApp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherApp.realm.WeatherForecastInfo
import com.example.weatherApp.apiResponseDataClasses.HourlyWeatherInfoResponse
import com.example.weatherApp.retrofit.RetrofitInstance
import com.example.weatherApp.apiResponseDataClasses.FullWeatherDataResponse
import com.example.weatherApp.retrofit.weatherResponseService
import com.example.weatherApp.apiResponseDataClasses.TemperatureValueResponse
import com.example.weatherApp.apiResponseDataClasses.WeatherResponse
import com.example.weatherApp.apiResponseDataClasses.WindResponse
import io.realm.Realm
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat


class WeatherDataForecastViewModel : ViewModel() {

    private var temperatureServices: weatherResponseService
    private val realm = Realm.getDefaultInstance()
    private var weatherData = MutableLiveData<List<HourlyWeatherInfoResponse>>()
    val weatherLiveData: LiveData<List<HourlyWeatherInfoResponse>> = weatherData

    private var weatherInfo: MutableLiveData<com.example.weatherApp.WeatherForecastInfo> = MutableLiveData()
    val weatherInfoLiveData: LiveData<com.example.weatherApp.WeatherForecastInfo>
        get() = weatherInfo


    init {
        temperatureServices =
            RetrofitInstance.getRetrofitInstance().create(weatherResponseService::class.java)
    }

    fun fetchWeatherForecastAndSaveToRealm(lat: Double, lon: Double, appid: String) {
        val call = temperatureServices.getWeatherForecast(lat, lon, appid)
        call.enqueue(object : Callback<FullWeatherDataResponse> {
            override fun onResponse(
                call: Call<FullWeatherDataResponse>,
                response: Response<FullWeatherDataResponse>
            ) {
                if (response.isSuccessful) {
                    val weatherResponse = response.body()?.list ?: emptyList()
                    weatherData.postValue(weatherResponse)

                    setDataOnViews(weatherResponse)

                    realm.executeTransaction { realm ->
                        realm.where(WeatherForecastInfo::class.java).findAll().deleteAllFromRealm()

                        weatherResponse?.forEach { item ->
                            val realmObject = WeatherForecastInfo()
                            realmObject.date = item.dt_txt
                            realmObject.time = item.dt_txt
                            realmObject.icon = item.weather[0].icon
                            realmObject.temperatureType = item.weather[0].main
                            realmObject.maxTemperature = item.main.temp_max
                            realmObject.minTemperature = item.main.temp_min
                            realmObject.temperature = item.main.temp
                            realmObject.windSpeed = item.wind.speed
                            realm.copyToRealm(realmObject)
                        }
                    }
                } else {
                    val realmData = getDataFromRealm()
                    weatherData.postValue(realmData)
                }
            }

            override fun onFailure(call: Call<FullWeatherDataResponse>, t: Throwable) {
                val realmData = getDataFromRealm()
                weatherData.postValue(realmData)
            }
        })
    }

    fun setDataOnViews(weatherResponse: List<HourlyWeatherInfoResponse>) {

            val temperatureValue = weatherResponse[0].main.temp - 273.15
            val temperature = temperatureValue.toInt()

            val type = weatherResponse[0].weather[0].main

            val inputDate = weatherResponse[0].dt_txt
            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val outputFormat = SimpleDateFormat("MMM d")
            val outputDate = inputFormat.parse(inputDate)
            val date = outputFormat.format(outputDate)

            val minTemperatureValue = weatherResponse[0].main.temp_min - 273.15
            val minTemperature = minTemperatureValue.toInt()

            val maxTemperatureValue = weatherResponse[0].main.temp_max - 273.15
            val maxTemperature = maxTemperatureValue.toInt()
            val icon = weatherResponse[0].weather[0].icon

            viewModelScope.launch(Dispatchers.Main) {
                weatherInfo.value = com.example.weatherApp.WeatherForecastInfo(
                    temperature,
                    type,
                    date,
                    minTemperature,
                    maxTemperature,
                    icon
                )
            }

    }

    override fun onCleared() {
        super.onCleared()
        realm.close()
    }

    fun getDataFromRealm(): List<HourlyWeatherInfoResponse> {
        viewModelScope.launch {
            val realm = Realm.getDefaultInstance()
        }
        val results = realm.where(WeatherForecastInfo::class.java).findAll()
        val weatherInfoList = mutableListOf<HourlyWeatherInfoResponse>()
        results.forEach { item ->
            val hourlyWeather = HourlyWeatherInfoResponse(
                TemperatureValueResponse(
                    item.temperature,
                    item.minTemperature,
                    item.maxTemperature
                ),
                listOf(WeatherResponse(item.temperatureType, item.icon)),
                WindResponse(item.windSpeed),
                item.date
            )
            weatherInfoList.add(hourlyWeather)
        }
        return weatherInfoList
    }
}