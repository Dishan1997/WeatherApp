package com.example.weatherApp.dao

import com.example.weatherApp.WeatherInfo
import com.example.weatherApp.apiResponse.HourlyWeatherInfoResponse
import com.example.weatherApp.apiResponse.TemperatureValueResponse
import com.example.weatherApp.apiResponse.WeatherResponse
import com.example.weatherApp.apiResponse.WindResponse
import com.example.weatherApp.realm.CurrentWeatherInfo
import com.example.weatherApp.realm.HourlyWeatherInfo
import com.example.weatherApp.realm.WeatherForecast
import io.realm.Realm

class WeatherInfoDao {
    private val realm = Realm.getDefaultInstance()
    fun getCurrentDataFromRealm(): WeatherInfo {
        var realm = Realm.getDefaultInstance()
        val results = realm.where(WeatherForecast::class.java).findAll()
        var currentWeatherdata = WeatherInfo("", "", 0.0)
        results.forEach { item ->
            val currentWeather = WeatherInfo(
                item.temperatureType, item.icon, item.temperature
            )
            currentWeatherdata = currentWeather
        }
        return currentWeatherdata
    }

    fun getHourlyDataFromRealm(): List<HourlyWeatherInfoResponse> {
       var realm = Realm.getDefaultInstance()
        val results = realm.where(WeatherForecast::class.java).findAll()
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

     fun saveCurrentDataToRealm(weatherData: WeatherInfo) {
        realm.executeTransaction { realm ->
            realm.where(CurrentWeatherInfo::class.java).findAll().deleteAllFromRealm()
            val realmObject = CurrentWeatherInfo()
            realmObject.icon = weatherData.icon
            realmObject.temperatureType = weatherData.main
            realmObject.temperature = weatherData.temperature
            realm.copyToRealm(realmObject)
        }
    }

     fun saveHourlyDataToRealm(weatherData: List<HourlyWeatherInfoResponse>) {
        realm.executeTransaction { realm ->
            realm.where(HourlyWeatherInfo::class.java).findAll().deleteAllFromRealm()
            weatherData.forEach { item ->
                val realmObject = HourlyWeatherInfo()
                realmObject.time = item.dt_txt
                realmObject.icon = item.weather[0].icon
                realmObject.temperature = item.main.temp
                realmObject.windSpeed = item.wind.speed
                realm.copyToRealm(realmObject)
            }
        }
    }

    fun closeRealm(){
        realm.close()
    }

}