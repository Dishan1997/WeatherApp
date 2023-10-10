package com.example.weatherApp.fragments

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.getlocation.databinding.WeatherForecastBinding
import com.example.weatherApp.ConstantKeys
import com.example.weatherApp.adapter.WeatherDataForecastActivityAdapter
import com.example.weatherApp.viewmodels.WeatherDataForecastViewModel

class WeatherDataForecastFragment : Fragment() {

    private var _binding: WeatherForecastBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerviewAdapter: WeatherDataForecastActivityAdapter
    private lateinit var viewModel: WeatherDataForecastViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       _binding = WeatherForecastBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[WeatherDataForecastViewModel::class.java]

        val bundle = arguments
        val lat = bundle?.getDouble("latitude")
        val lon = bundle?.getDouble("longitude")
        val cityName = bundle?.getString("cityName")
        binding.cityTextView.text = cityName

        binding.weatherHistoryDataRecyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerviewAdapter = WeatherDataForecastActivityAdapter()

        if (lon != null && lat != null) {
            viewModel.fetchWeatherForecastAndSaveToRealm(lat, lon, ConstantKeys.appid)
        }

        viewModel.weatherInfoLiveData.observe(viewLifecycleOwner, Observer {
            binding.tempTextView.text = it.temperature.toString()
            binding.weatherTypeTextView.text = it.type
            binding.maxTempTextView.text = it.maxTemperature.toString() + "ºC /  "
            binding.minTempTextView.text = it.minTemperature.toString() + "ºC"
            binding.dateTextView.text = it.date
            var url = "${ConstantKeys.ICON_URL}" + it.wetherIcon + ".png"
            Glide.with(binding.root).load(url).into(binding.currentWeatherIconImageView)
        })
         viewModel.weatherLiveData.observe(viewLifecycleOwner, Observer { weatherData ->
            recyclerviewAdapter.loadWeatherData(weatherData)

        })
        binding.weatherHistoryDataRecyclerView.adapter = recyclerviewAdapter
       setFragmentResult("fragmentKey", bundleOf("getCityNameFromFragment" to cityName))
    }
}