package com.example.weatherApp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherApp.apiResponse.HourlyWeatherInfoResponse
import com.example.weatherApp.WeatherInfo
import com.example.weatherApp.repository.WeatherInfoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class WeatherInfoViewModel(private val repository: WeatherInfoRepository) : ViewModel(){

    private var weatherInfo: MutableLiveData<WeatherInfo> = MutableLiveData()
    private var listOfWeatherInfos: MutableLiveData<List<HourlyWeatherInfoResponse>> =
        MutableLiveData()
    val weatherInfoLiveData: LiveData<WeatherInfo>
        get() = weatherInfo

    val listOfWeatherInfoLiveData: LiveData<List<HourlyWeatherInfoResponse>>
        get() = listOfWeatherInfos


     fun fetchWeatherInfo(lat: Double, long: Double) {
        repository.fetchWeatherInfo(lat, long) {
            weatherData, errorMessage->
            if(weatherData == null){
                val data = repository.getCurrentDataFromRealm()
                viewModelScope.launch(Dispatchers.Main) {
                    weatherInfo.value = data!!
                }
            }
            else{
                viewModelScope.launch(Dispatchers.Main) {
                    weatherInfo.value = weatherData
                }
            }
        }
        }


     fun fetchWeatherInfoHourly(lat: Double, long: Double) {
        repository.fetchWeatherInfoHourly(lat, long){
                hourlyWeatherData, errorMessage->
            if(hourlyWeatherData == null){
               val data = repository.getHourlyDataFromRealm() ?: emptyList()
                viewModelScope.launch(Dispatchers.Main) {
                    listOfWeatherInfos.postValue(data)
                }
            }
            else{
                viewModelScope.launch(Dispatchers.Main) {
                    listOfWeatherInfos.postValue(hourlyWeatherData)
                }
            }
        }
    }
}

