package com.example.weatherApp.repository

import com.example.weatherApp.ConstantKeys
import com.example.weatherApp.WeatherInfo
import com.example.weatherApp.apiResponse.HourlyWeatherInfoResponse
import com.example.weatherApp.dao.WeatherInfoDao
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.lang.StringBuilder
import java.lang.reflect.Type
import java.net.HttpURLConnection
import java.net.URL

class WeatherInfoRepository(private val dao: WeatherInfoDao) {

       fun fetchWeatherInfo(lat: Double, long: Double, weatherInfoCallback: (info: WeatherInfo?, errorMessage : String?) -> Unit ) {
          var httpURLConnection : HttpURLConnection ?= null
        try {
            val url =
                URL("${ConstantKeys.BASE_URL}data/2.5/weather?lat=$lat&lon=$long&appid=${ConstantKeys.APP_ID}")
            httpURLConnection = url.openConnection() as HttpURLConnection
            val code = httpURLConnection?.responseCode
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
                var c = temperature - ConstantKeys.KELVIN_TO_CELCIUS_VALUE
                val celcius = String.format("%.2f", c)
                var iconUrl = "${ConstantKeys.ICON_URL}" + icon + ".png"

            if(main!=null && iconUrl!=null && celcius!=null) {
                val currentWeatherData = WeatherInfo(main, iconUrl, celcius.toDouble())
                CoroutineScope(Dispatchers.Main).launch {
                    dao.saveCurrentWeatherData(currentWeatherData)
                }
                weatherInfoCallback(currentWeatherData, null)
            }
        }catch (e: IOException) {
           weatherInfoCallback(null, "no data found")
        } finally {
            httpURLConnection?.disconnect()
        }
    }

      fun fetchWeatherInfoHourly(lat: Double, long: Double, weatherInfoCallback: (info: List<HourlyWeatherInfoResponse>?, errorMessage : String?) -> Unit ) {
        var httpURLConnection: HttpURLConnection? = null
        try {
            val url =
                URL("${ConstantKeys.BASE_URL}data/2.5/forecast?lat=$lat&lon=$long&appid=${ConstantKeys.APP_ID}")
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
            val weatherList = jsonObject.getJSONArray("list")
            val type: Type = object : TypeToken<List<HourlyWeatherInfoResponse?>?>() {}.type
            var weatherDataList: List<HourlyWeatherInfoResponse>  = Gson().fromJson(weatherList.toString(), type)
            CoroutineScope(Dispatchers.Main).launch{
                dao.saveHourlyWeatherData(weatherDataList)
            }
            weatherInfoCallback(weatherDataList, null)

        } catch (e: IOException) {
            weatherInfoCallback(null, "no data found")
        } finally {
            httpURLConnection?.disconnect()
        }
    }

    fun getCurrentDataFromRealm(): WeatherInfo? {
        val realmData =  dao.getCurrentWeatherData()
        return realmData
    }

    fun getHourlyDataFromRealm(): List<HourlyWeatherInfoResponse>? {
        val realmData =  dao.getHourlyWeatherData()
        return realmData
    }

}