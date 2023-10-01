package com.example.weatherApp.viewmodels

import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherApp.ConstantKeys
import com.example.weatherApp.CurrentWeatherInfo
import com.example.weatherApp.HourlyWeatherInfo
import com.example.weatherApp.apiResponseDataClasses.HourlyWeatherInfoResponse
import com.example.weatherApp.WeatherInfo
import com.example.weatherApp.apiResponseDataClasses.TemperatureValueResponse
import com.example.weatherApp.apiResponseDataClasses.WeatherResponse
import com.example.weatherApp.apiResponseDataClasses.WindResponse
import com.example.weatherApp.realm.WeatherForecastInfo
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.realm.Realm
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.lang.StringBuilder
import java.lang.reflect.Type
import java.net.HttpURLConnection
import java.net.URL

class WeatherInfoViewModel : ViewModel() {

    private var weatherInfo: MutableLiveData<WeatherInfo> = MutableLiveData()
    private var listOfWeatherInfos: MutableLiveData<List<HourlyWeatherInfoResponse>> =
        MutableLiveData()
    val weatherInfoLiveData: LiveData<WeatherInfo>
        get() = weatherInfo

    val listOfWeatherInfoLiveData: LiveData<List<HourlyWeatherInfoResponse>>
        get() = listOfWeatherInfos


    suspend fun fetchWeatherInfo(lat: Double, long: Double) {

        var httpURLConnection: HttpURLConnection? = null
        try {
            val url =
                URL("https://api.openweathermap.org/data/2.5/weather?lat=$lat&lon=$long&appid=${ConstantKeys.appid}")
            httpURLConnection = url.openConnection() as HttpURLConnection
            val code = httpURLConnection.responseCode
            if (code != 200) {
                throw IOException("The error from the server is $code")
            }
            val bufferedReader = BufferedReader(
                InputStreamReader(httpURLConnection.inputStream)
            )
            val jsonStringHolder = StringBuilder()
            while (true) {
                val readLine = bufferedReader.readLine() ?: break
                jsonStringHolder.append(readLine)
            }
            val jsonObject = JSONObject(jsonStringHolder.toString())
            val weatherDataList = jsonObject.getJSONArray("weather")
            val weatherData = weatherDataList[0] as JSONObject
            val main = weatherData.getString("main")
            val icon = weatherData.getString("icon")
            val mainData = jsonObject.getJSONObject("main")
            val temperature = mainData.getDouble("temp")
            var c = temperature - 273.15
            val celcius = String.format("%.2f", c)
            var uri = Uri.parse("https://openweathermap.org/img/w/" + icon + ".png")
            viewModelScope.launch(Dispatchers.Main) {
                weatherInfo.value = WeatherInfo(main, uri, celcius.toDouble())
            }
            val currentWeatherData = WeatherInfo(main, uri, celcius.toDouble())

            saveCurrentDataToRealm(currentWeatherData)

        } catch (ioexception: IOException) {
            Log.e(this.javaClass.name, ioexception.message.toString())
            viewModelScope.launch(Dispatchers.Main){
                val realmData = getCurrentDataFromRealm()
                weatherInfo.value = realmData
            }
        } finally {
            httpURLConnection?.disconnect()
        }
    }

    private fun saveCurrentDataToRealm(weatherData : WeatherInfo) {
        val realmObject = CurrentWeatherInfo()
        realmObject.icon = weatherData.icon.toString()
        realmObject.temperatureType = weatherData.main
        realmObject.temperature = weatherData.temperature
    }

    private fun saveHourlyDataToRealm(weatherData :List<HourlyWeatherInfoResponse>) {
      weatherData.forEach{item->
       val realmObject = HourlyWeatherInfo()
          realmObject.time = item.dt_txt
          realmObject.icon = item.weather[0].icon
          realmObject.temperature = item.main.temp
          realmObject.windSpeed = item.wind.speed
      }
    }


    suspend fun fetchWeatherInfoHourly(lat: Double, long: Double) {
        var newList: List<HourlyWeatherInfoResponse> = listOf()
        var httpURLConnection: HttpURLConnection? = null
        try {
            val url =
                URL("https://api.openweathermap.org/data/2.5/forecast?lat=$lat&lon=$long&appid=${ConstantKeys.appid}")
            httpURLConnection = url.openConnection() as HttpURLConnection
            val code = httpURLConnection.responseCode
            if (code != 200) {
                throw IOException("The error from the server is $code")
            }
            val bufferedReader = BufferedReader(
                InputStreamReader(httpURLConnection.inputStream)
            )
            val jsonStringHolder: StringBuilder = StringBuilder()
            while (true) {
                val readLine = bufferedReader.readLine() ?: break
                jsonStringHolder.append(readLine)
            }
            val jsonObject = JSONObject(jsonStringHolder.toString())
            val weatherList = jsonObject.getJSONArray("list")
            val type: Type = object : TypeToken<List<HourlyWeatherInfoResponse?>?>() {}.type
            newList = Gson().fromJson(weatherList.toString(), type)

            saveHourlyDataToRealm(newList)

            viewModelScope.launch(Dispatchers.Main) {
                listOfWeatherInfos.value = newList
            }

        } catch (ioexception: IOException) {
            Log.e(this.javaClass.name, ioexception.message.toString())
            viewModelScope.launch (Dispatchers.Main){
                val realmData = getHourlyDataFromRealm()
                listOfWeatherInfos.value = realmData
            }

        } finally {
            httpURLConnection?.disconnect()
        }
    }

    fun getHourlyDataFromRealm(): List<HourlyWeatherInfoResponse> {
        var realm = Realm.getDefaultInstance()
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

    fun getCurrentDataFromRealm(): WeatherInfo {
        var realm = Realm.getDefaultInstance()
        val results = realm.where(WeatherForecastInfo::class.java).findAll()
        var currentWeatherdata = WeatherInfo("", Uri.EMPTY, 0.0)
        results.forEach { item ->
            val currentWeather = WeatherInfo(
                item.temperatureType, item.icon.toUri(), item.temperature
            )
            currentWeatherdata = currentWeather
        }
        return currentWeatherdata
    }
    override fun onCleared() {
        super.onCleared()
        var realm = Realm.getDefaultInstance()
        realm.close()
    }

}