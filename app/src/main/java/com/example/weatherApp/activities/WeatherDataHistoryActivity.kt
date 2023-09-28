package com.example.weatherApp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.weatherApp.ConstantKeys
import com.example.weatherApp.adapter.ThirdActivityAdapter
import com.example.weatherApp.viewmodels.WeatherDataHistoryViewModel
import com.example.getlocation.databinding.WeatherHistoryBinding
import io.realm.Realm

class WeatherDataHistoryActivity : AppCompatActivity() {
    private lateinit var binding: WeatherHistoryBinding
    private lateinit var recyclerviewAdapter: ThirdActivityAdapter
    var realm = Realm.getDefaultInstance()
    private lateinit var viewModel: WeatherDataHistoryViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = WeatherHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[WeatherDataHistoryViewModel::class.java]

        val intent = Intent(this, WeatherInfoActivity::class.java)
        val lat = intent.getDoubleExtra("latitude", 0.0)
        val lon = intent.getDoubleExtra("longitude", 0.0)
        val cityName = intent.getStringExtra("getCityName")
        Log.i("mytag","city name = $cityName   lat = $lat   long = $lon")
        binding.cityTextView.text = "The Westin Dhaka"

        binding.weatherHistoryDataRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerviewAdapter = ThirdActivityAdapter()

        viewModel.fetchWeatherForecastAndSaveToRealm(lat, lon, ConstantKeys.appid)

        viewModel.weatherInfoLiveData.observe(this, Observer {
            binding.tempTextView.text = it.temperature.toString()
            binding.weatherTypeTextView.text = it.type
            binding.maxTempTextView.text = it.maxTemperature.toString() + "ºC/"
            binding.minTempTextView.text = it.minTemperature.toString() + "ºC"
            binding.dateTextView.text = it.date
        })

        viewModel.weatherLiveData.observe(this, Observer { weatherData ->
            recyclerviewAdapter.initWeather(weatherData)

        })
        binding.weatherHistoryDataRecyclerView.adapter = recyclerviewAdapter
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }

}