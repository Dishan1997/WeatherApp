package com.example.weatherApp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherApp.apiResponse.HourlyWeatherInfoResponse
import com.example.weatherApp.WeatherInfo
import com.example.weatherApp.WeatherInfoCallBack
import com.example.weatherApp.repository.WeatherInfoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class WeatherInfoViewModel(private val repository: WeatherInfoRepository) : ViewModel(),
    WeatherInfoCallBack {

    init{
        repository.weatherInfoCallback = this
    }
    private var weatherInfo: MutableLiveData<WeatherInfo> = MutableLiveData()
    private var listOfWeatherInfos: MutableLiveData<List<HourlyWeatherInfoResponse>> =
        MutableLiveData()
    val weatherInfoLiveData: LiveData<WeatherInfo>
        get() = weatherInfo

    val listOfWeatherInfoLiveData: LiveData<List<HourlyWeatherInfoResponse>>
        get() = listOfWeatherInfos


    suspend fun fetchWeatherInfo(lat: Double, long: Double) {
        repository.fetchWeatherInfo(lat, long).let{
            if(it == null){
                repository.getCurrentDataFromRealm()
            }
        }
    }

    suspend fun fetchWeatherInfoHourly(lat: Double, long: Double) {
        repository.fetchWeatherInfoHourly(lat, long).let{
            if(it == null){
                repository.getHourlyDataFromRealm()
            }
        }

    }
    override fun onCleared() {
        repository.weatherInfoCallback = null
        repository.closeRealm()
        super.onCleared()

    }
    override fun onWeatherInfoFetched(weatherInfoData: WeatherInfo) {
        viewModelScope.launch(Dispatchers.Main) {
            weatherInfo.value = weatherInfoData
        }
    }
    override fun onHourlyWeatherInfoFetched(hourlyWeatherInfo: List<HourlyWeatherInfoResponse>) {
        viewModelScope.launch(Dispatchers.Main) {
            listOfWeatherInfos.postValue(hourlyWeatherInfo)
        }
    }
    override fun onWeatherInfoFailure(errorResult: String) {
    }
    override fun onHourlyWeatherInfoFailure(errorResult: String) {
    }

}