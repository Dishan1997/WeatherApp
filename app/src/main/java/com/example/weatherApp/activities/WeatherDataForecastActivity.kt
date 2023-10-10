package com.example.weatherApp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.getlocation.databinding.WeatherForecastBinding
import com.example.weatherApp.ConstantKeys
import com.example.weatherApp.adapter.WeatherDataForecastActivityAdapter
import com.example.weatherApp.viewmodels.WeatherDataForecastViewModel

class WeatherDataForecastActivity : AppCompatActivity() {
    private lateinit var binding: WeatherForecastBinding
    private lateinit var recyclerviewAdapter: WeatherDataForecastActivityAdapter
    private lateinit var viewModel: WeatherDataForecastViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = WeatherForecastBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[WeatherDataForecastViewModel::class.java]

         val intent = getIntent()
        val lat = intent.getDoubleExtra(ConstantKeys.KEY_LATITUDE, 0.0)
        val lon = intent.getDoubleExtra(ConstantKeys.KEY_LONGITUDE, 0.0)
        val cityName = intent.getStringExtra(ConstantKeys.KEY_CITY_NAME)
        binding.cityTextView.text = cityName

        binding.weatherHistoryDataRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerviewAdapter = WeatherDataForecastActivityAdapter()

        viewModel.fetchWeatherForecastAndSaveToRealm(lat, lon, ConstantKeys.APP_ID)

        viewModel.weatherInfoLiveData.observe(this, Observer {
            binding.tempTextView.text = it.temperature.toString()
            binding.weatherTypeTextView.text = it.type
            binding.maxTempTextView.text = it.maxTemperature.toString() + "ºC /  "
            binding.minTempTextView.text = it.minTemperature.toString() + "ºC"
            binding.dateTextView.text = it.date
            var url = "${ConstantKeys.ICON_URL}" + it.wetherIcon + ".png"
            Glide.with(binding.root).load(url).into(binding.currentWeatherIconImageView)
        })

        viewModel.weatherLiveData.observe(this, Observer { weatherData ->
            recyclerviewAdapter.loadWeatherData(weatherData)

        })
        binding.weatherHistoryDataRecyclerView.adapter = recyclerviewAdapter
    }
}