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

    fun getCurrentWeatherData(): WeatherInfo? {
        var realm = Realm.getDefaultInstance()
        val results = realm.where(WeatherForecast::class.java).findAll()
        var size = results.size
        if(results!=null) {
            val temperatureType= results[size-1]?.temperatureType ?: ""
            val icon = results[size-1]?.icon ?: ""
            val temperature = results[size-1]?.temperature ?: 0.0

                val currentWeather = WeatherInfo(
                    temperatureType, icon, temperature
                )
            return currentWeather
        }
        return null
    }

    fun getHourlyWeatherData(): List<HourlyWeatherInfoResponse>? {
       var realm = Realm.getDefaultInstance()
        val results = realm.where(WeatherForecast::class.java).findAll()
        val weatherInfoList = mutableListOf<HourlyWeatherInfoResponse>()
        if(results.isNotEmpty()) {
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
        return null
    }

     fun saveCurrentWeatherData(weatherData: WeatherInfo) {
         Realm.getDefaultInstance().use {realm->
             realm.executeTransaction { realm ->
                 realm.where(CurrentWeatherInfo::class.java).findAll().deleteAllFromRealm()
                 val realmObject = CurrentWeatherInfo()
                 realmObject.icon = weatherData.icon
                 realmObject.temperatureType = weatherData.main
                 realmObject.temperature = weatherData.temperature
                 realm.copyToRealm(realmObject)
             }
         }
    }

     fun saveHourlyWeatherData(weatherData: List<HourlyWeatherInfoResponse>) {
        Realm.getDefaultInstance().use {realm->
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

    }

}