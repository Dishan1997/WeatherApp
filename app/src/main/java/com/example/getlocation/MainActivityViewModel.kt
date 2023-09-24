package com.example.getlocation

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
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

class MainActivityViewModel : ViewModel() {

    private var weatherInfo: MutableLiveData<WeatherInfo> = MutableLiveData()
   private var listOfWeatherInfos : MutableLiveData<List<HourlyWeatherInfo>> = MutableLiveData()
    val weatherInfoLiveData: LiveData<WeatherInfo>
        get() = weatherInfo

    val listOfWeatherInfoLiveData : LiveData<List<HourlyWeatherInfo>>
        get() = listOfWeatherInfos


    suspend fun fetchWeatherInfo(lat: Double, long: Double) {

        var httpURLConnection: HttpURLConnection? = null
        try {
            val url =
                URL("https://api.openweathermap.org/data/2.5/weather?lat=$lat&lon=$long&appid=80c089997f86207df0e810e2f184982a")

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
            Log.i("mytag", "temperature = " + "$temperature")
            var c = temperature - 273.15
            val celcius = String.format("%.2f", c)
            var uri = Uri.parse("https://openweathermap.org/img/w/" + icon + ".png")

            viewModelScope.launch(Dispatchers.Main) {
                weatherInfo.value = WeatherInfo(main, uri, celcius.toDouble())
            }


        } catch (ioexception: IOException) {
            Log.e(this.javaClass.name, ioexception.message.toString())
        } finally {
            httpURLConnection?.disconnect()
        }
        Log.i("mytag", "inside coroutine")

        Log.i("mytag", "outside coroutine")
    }

    suspend fun fetchWeatherInfoHourly(lat: Double, long: Double){
        var newList : List<HourlyWeatherInfo> = listOf()

        var httpURLConnection: HttpURLConnection? = null
        try {

            val url =
                URL("https://api.openweathermap.org/data/2.5/forecast?lat=$lat&lon=$long&appid=80c089997f86207df0e810e2f184982a")

            Log.i("mytag","$url")

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
            val type: Type = object : TypeToken<List<HourlyWeatherInfo?>?>() {}.type

            Log.i("mytag", " type = $type")
            newList = Gson().fromJson(weatherList.toString(), type)

             viewModelScope.launch(Dispatchers.Main) {
                 listOfWeatherInfos.value = newList
             }

        } catch (ioexception: IOException) {
            Log.e(this.javaClass.name, ioexception.message.toString())
        } finally {
            httpURLConnection?.disconnect()
        }
    }

}