package com.example.weatherApp.repository

import android.util.Log
import com.example.weatherApp.ConstantKeys
import com.example.weatherApp.WeatherInfo
import com.example.weatherApp.WeatherInfoCallBack
import com.example.weatherApp.apiResponse.HourlyWeatherInfoResponse
import com.example.weatherApp.dao.WeatherInfoDao
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.lang.StringBuilder
import java.lang.reflect.Type
import java.net.HttpURLConnection
import java.net.URL
import kotlin.coroutines.resume

class WeatherInfoRepository(private val dao: WeatherInfoDao) {
    private var weatherInfo = WeatherInfo("", "", 0.0)
    private var listOfWeatherInfos: List<HourlyWeatherInfoResponse> = listOf()
     var weatherInfoCallback: WeatherInfoCallBack? = null


    suspend fun fetchWeatherInfo(lat: Double, long: Double) {
        var httpURLConnection: HttpURLConnection? = null
        try {
            val url =
                URL("${ConstantKeys.BASE_URL}data/2.5/weather?lat=$lat&lon=$long&appid=${ConstantKeys.APP_ID}")
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
            var iconUrl = "${ConstantKeys.ICON_URL}" + icon + ".png"
            weatherInfo = WeatherInfo(main, iconUrl, celcius.toDouble())

            val currentWeatherData = WeatherInfo(main, iconUrl, celcius.toDouble())
            GlobalScope.launch(Dispatchers.Main) {
                dao.saveCurrentDataToRealm(currentWeatherData)
            }
            weatherInfoCallback?.onWeatherInfoFetched(weatherInfo)

        } catch (e: IOException) {
            weatherInfoCallback?.onWeatherInfoFailure(e.message ?: "An error occurred")
        } finally {
            httpURLConnection?.disconnect()
        }
    }

    suspend fun fetchWeatherInfoHourly(lat: Double, long: Double) {

        var weatherDataList: List<HourlyWeatherInfoResponse> = listOf()
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
            val jsonStringHolder: StringBuilder = StringBuilder()
            while (true) {
                val readLine = bufferedReader.readLine() ?: break
                jsonStringHolder.append(readLine)
            }
            val jsonObject = JSONObject(jsonStringHolder.toString())
            val weatherList = jsonObject.getJSONArray("list")
            val type: Type = object : TypeToken<List<HourlyWeatherInfoResponse?>?>() {}.type
            weatherDataList = Gson().fromJson(weatherList.toString(), type)
            GlobalScope.launch(Dispatchers.Main) {
                dao.saveHourlyDataToRealm(weatherDataList)
            }
            listOfWeatherInfos = weatherDataList
            weatherInfoCallback?.onHourlyWeatherInfoFetched(listOfWeatherInfos)

        } catch (e: IOException) {
            weatherInfoCallback?.onHourlyWeatherInfoFailure(e.message ?: "An error occurred")
        } finally {
            httpURLConnection?.disconnect()
        }
    }

    fun getCurrentDataFromRealm(): WeatherInfo? {
        val realmData =  dao.getCurrentDataFromRealm()
        weatherInfo = realmData!!
        weatherInfoCallback?.onWeatherInfoFetched(weatherInfo)
        return realmData
    }

    fun getHourlyDataFromRealm(): List<HourlyWeatherInfoResponse>? {
        val realmData =  dao.getHourlyDataFromRealm()
        listOfWeatherInfos = realmData!!
        weatherInfoCallback?.onHourlyWeatherInfoFetched(listOfWeatherInfos)
        return realmData
    }


    fun closeRealm() {
        dao.closeRealm()
    }

}