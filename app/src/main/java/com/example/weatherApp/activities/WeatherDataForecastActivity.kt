package com.example.weatherApp.activities

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.getlocation.databinding.WeatherForecastBinding
import com.example.weatherApp.ConstantKeys
import com.example.weatherApp.adapter.ThirdActivityAdapter
import com.example.weatherApp.viewmodels.WeatherDataForecastViewModel
import io.realm.Realm

class WeatherDataForecastActivity : AppCompatActivity() {
    private lateinit var binding: WeatherForecastBinding
    private lateinit var recyclerviewAdapter: ThirdActivityAdapter
    private lateinit var viewModel: WeatherDataForecastViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = WeatherForecastBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[WeatherDataForecastViewModel::class.java]

         val intent = getIntent()
        val lat = intent.getDoubleExtra("latitude1", 0.0)
        val lon = intent.getDoubleExtra("longitude1", 0.0)
        val cityName = intent.getStringExtra("getCityName")
        binding.cityTextView.text = cityName

        binding.weatherHistoryDataRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerviewAdapter = ThirdActivityAdapter()

        viewModel.fetchWeatherForecastAndSaveToRealm(lat, lon, ConstantKeys.appid)

        viewModel.weatherInfoLiveData.observe(this, Observer {
            binding.tempTextView.text = it.temperature.toString()
            binding.weatherTypeTextView.text = it.type
            binding.maxTempTextView.text = it.maxTemperature.toString() + "ºC /  "
            binding.minTempTextView.text = it.minTemperature.toString() + "ºC"
            binding.dateTextView.text = it.date
            var uri = Uri.parse("https://openweathermap.org/img/w/" + it.wetherIcon + ".png")
            Glide.with(binding.root).load(uri).into(binding.currentWeatherIconImageView)
        })

        viewModel.weatherLiveData.observe(this, Observer { weatherData ->
            recyclerviewAdapter.initWeather(weatherData)

        })
        binding.weatherHistoryDataRecyclerView.adapter = recyclerviewAdapter
    }

    override fun onDestroy() {
        super.onDestroy()
        var realm = Realm.getDefaultInstance()
        realm.close()
    }

}