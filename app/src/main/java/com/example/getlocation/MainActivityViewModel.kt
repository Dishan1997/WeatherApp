package com.example.getlocation

import android.net.Uri
import android.util.Log
import android.widget.TextView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.example.getlocation.databinding.ActivityMainBinding
import com.example.getlocation.databinding.DesignLayoutBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.lang.StringBuilder
import java.lang.reflect.Type
import java.net.HttpURLConnection
import java.net.URL

class MainActivityViewModel : ViewModel() {

     fun makeGetApiRequest(binding : ActivityMainBinding, lat: Double, long: Double) {

        viewModelScope.launch(Dispatchers.IO) {
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



                viewModelScope.launch(Dispatchers.Main) {
                    binding.apply {
                        textViewWeatherType.text = main
                        var uri = Uri.parse("https://openweathermap.org/img/w/" + icon + ".png")
                       // Glide.with(this).load(uri).into(imageViewWeather)
                    }
                    binding.apply {
                        var c = temperature - 273.15
                        val celcius = String.format("%.2f", c)
                        textViewTemperature.text = celcius + "ºC"
                    }

                }

            } catch (ioexception: IOException) {
                Log.e(this.javaClass.name, ioexception.message.toString())
            } finally {
                httpURLConnection?.disconnect()
            }
            Log.i("mytag", "inside coroutine")
        }
        Log.i("mytag", "outside coroutine")

    }


 suspend fun getApiDataForThreeHours(lat: Double, long: Double) : List<ListofData>{
        var newList : List<ListofData> = listOf()

       viewModelScope.launch(Dispatchers.IO){
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
                val type: Type = object : TypeToken<List<ListofData?>?>() {}.type

                Log.i("mytag", " type = $type")
                newList = Gson().fromJson(weatherList.toString(), type)

                Log.i("mytag", "newList = $newList")
                Log.i("mytag", "inside data")


            } catch (ioexception: IOException) {
                Log.e(this.javaClass.name, ioexception.message.toString())
            } finally {
                httpURLConnection?.disconnect()
            }
        }
     delay(100000)
        return newList
    }

}