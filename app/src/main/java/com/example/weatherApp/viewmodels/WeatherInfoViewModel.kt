package com.example.weatherApp.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherApp.ConstantKeys
import com.example.weatherApp.realm.CurrentWeatherInfo
import com.example.weatherApp.realm.HourlyWeatherInfo
import com.example.weatherApp.apiResponse.HourlyWeatherInfoResponse
import com.example.weatherApp.WeatherInfo
import com.example.weatherApp.WeatherInfoCallBack
import com.example.weatherApp.apiResponse.TemperatureValueResponse
import com.example.weatherApp.apiResponse.WeatherResponse
import com.example.weatherApp.apiResponse.WindResponse
import com.example.weatherApp.realm.WeatherForecast
import com.example.weatherApp.repository.WeatherInfoRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.realm.Realm
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.lang.Exception
import java.lang.StringBuilder
import java.lang.reflect.Type
import java.net.HttpURLConnection
import java.net.URL

class WeatherInfoViewModel(private val repository: WeatherInfoRepository) : ViewModel(),
    WeatherInfoCallBack {

    private var weatherInfo: MutableLiveData<WeatherInfo> = MutableLiveData()
    private var listOfWeatherInfos: MutableLiveData<List<HourlyWeatherInfoResponse>> =
        MutableLiveData()
    val weatherInfoLiveData: LiveData<WeatherInfo>
        get() = weatherInfo

    val listOfWeatherInfoLiveData: LiveData<List<HourlyWeatherInfoResponse>>
        get() = listOfWeatherInfos


    suspend fun fetchWeatherInfo(lat: Double, long: Double) {
        repository.setCallback(this)
        repository.fetchWeatherInfo(lat, long)
    }

    suspend fun fetchWeatherInfoHourly(lat: Double, long: Double) {
        repository.setCallback(this)
        repository.fetchWeatherInfoHourly(lat, long)
    }
    override fun onCleared() {
        super.onCleared()
        repository.closeRealm()
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