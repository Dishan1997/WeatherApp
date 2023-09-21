package com.example.getlocation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.getlocation.databinding.ActivityThirdBinding
import com.google.gson.Gson
import io.realm.Realm
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.URL

class ThirdActivity : AppCompatActivity() {
    private lateinit var binding :ActivityThirdBinding

    private lateinit var recyclerviewAdapter : ThirdActivityAdapter
    private lateinit var tempHistoryList : List<Temperature>
    var realm = Realm.getDefaultInstance()
    private lateinit var temp: Temperature
    private lateinit var retServices : TemperatureService


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
         binding = ActivityThirdBinding.inflate(layoutInflater)
        setContentView(binding.root)



        GlobalScope.launch {
            withContext(Dispatchers.Default){
               tempHistoryList =  getApiDataForTwoWeeks()
            }
        }

        retServices = RetrofitInstance.getRetrofitInstance().create(TemperatureService::class.java)
       // getRequestWithQueryParameter()

        binding.recyclerViewHistoryData.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerviewAdapter = ThirdActivityAdapter(tempHistoryList)
        binding.recyclerViewHistoryData.adapter = recyclerviewAdapter
    }


    suspend fun getApiDataForTwoWeeks(): List<Temperature> {

       // var appId = "80c089997f86207df0e810e2f184982a"
        var lat = 23.8041
        var long = 90.4152
        var temperatureResponse: List<Temperature> = listOf()

        CoroutineScope(Dispatchers.IO).launch {
            var httpURLConnection: HttpURLConnection? = null
            try {

                val url =
                    URL("https://api.openweathermap.org/data/2.5/forecast?lat=$lat&lon=$long&appid=80c089997f86207df0e810e2f184982a")

                httpURLConnection = url.openConnection() as HttpURLConnection

                val code = httpURLConnection.responseCode

                if (code != 200) {
                   // showTemperatureListOffline()
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

                temperatureResponse =
                    listOf(Gson().fromJson(jsonStringHolder.toString(), Temperature::class.java))

                  saveApiDataOnRealm()

            } catch (ioexception: IOException) {
                Log.e(this.javaClass.name, ioexception.message.toString())
            } finally {
                httpURLConnection?.disconnect()
            }
        }


        return temperatureResponse as ArrayList<Temperature>
    }

    /*private fun showTemperatureListOffline()  {
        val data = realm.where(TemperatureDatabase::class.java).findAll()
        val tempList = mutableListOf<Temperature>()

        for (realmObject in data) {
            val data = Temperature(realmObject.id, realmObject.temperature, realmObject.time, realmObject.wind)
            tempList.add(data)
        }

        recyclerviewAdapter = ThirdActivityAdapter(tempList)
        binding.recyclerViewHistoryData.adapter = recyclerviewAdapter

    }*/

    fun saveApiDataOnRealm(){
                realm.executeTransaction { realm ->
                    val objectTemp = realm.createObject(TemperatureDatabase:: class.java)
                    objectTemp.id = 0
                    objectTemp.temperature = temp.list[1].main.temp
                    objectTemp.time = temp.list[2].weather[0].main
                  //  objectTemp.icon =
                    objectTemp.wind = temp.list[4].wind.speed.toString()
                }
        Toast.makeText(this, "inside saveApi", Toast.LENGTH_LONG).show()
    }


//    private fun getRequestWithQueryParameter(){
//        val responseLiveData: LiveData<Response<Temperature>> = liveData {
//            val response: Response<Temperature> = retServices.getSortedAlbums(3)
//            emit(response)
//        }
//        responseLiveData.observe(this, Observer{
//            val albumList : MutableListIterator<Temperature>? = it.body()?.listIterator()
//            if(albumList != null){
//                while (albumList.hasNext()){
//                    val albumsItem = albumList.next()
//                    //Log.i("mytag", albumsItem.title)
//                    val result : String = "Album Title : ${albumsItem.title}" + "\n" +
//                            "Album id : ${albumsItem.id}" + "\n" +
//                            "User id : ${albumsItem.userId}" + "\n\n"
//                    binding.tvView.append(result)
//
//                }
//            }
//        })
//    }

}