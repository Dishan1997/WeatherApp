package com.example.weatherApp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.getlocation.databinding.WeatherForecastBinding
import com.example.weatherApp.ConstantKeys
import com.example.weatherApp.adapter.WeatherDataForecastActivityAdapter
import com.example.weatherApp.viewModelFactory.WeatherDataForecastViewModelFactory
import com.example.weatherApp.viewModelFactory.WeatherInfoViewModelFactory
import com.example.weatherApp.viewmodels.WeatherDataForecastViewModel
import com.example.weatherApp.viewmodels.WeatherInfoViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class WeatherDataForecastFragment : Fragment() {

    private var binding: WeatherForecastBinding? = null
    private var recyclerviewAdapter= WeatherDataForecastActivityAdapter()
    private lateinit var viewModel: WeatherDataForecastViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       binding = WeatherForecastBinding.inflate(layoutInflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.let{
            viewModel = ViewModelProvider(it, WeatherDataForecastViewModelFactory()).get(WeatherDataForecastViewModel::class.java)
        }
        val bundle = arguments
        val lat = bundle?.getDouble(ConstantKeys.KEY_LATITUDE)
        val lon = bundle?.getDouble(ConstantKeys.KEY_LONGITUDE)
        val cityName = bundle?.getString(ConstantKeys.KEY_CITY_NAME)
        binding?.cityTextView?.text = cityName

        binding?.weatherHistoryDataRecyclerView?.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        if (lon != null && lat != null) {
            GlobalScope.launch {
                viewModel.getWeatherForecast(lat, lon)
            }
        }

        viewModel.weatherInfoLiveData.observe(viewLifecycleOwner, Observer {
            binding?.tempTextView?.text = it.temperature.toString()
            binding?.weatherTypeTextView?.text = it.type
            binding?.maxTempTextView?.text = it.maxTemperature.toString() + "ºC /  "
            binding?.minTempTextView?.text = it.minTemperature.toString() + "ºC"
            binding?.dateTextView?.text = it.date
            var url = "${ConstantKeys.ICON_URL}" + it.wetherIcon + ".png"
            Glide.with(binding?.root!!).load(url).into(binding?.currentWeatherIconImageView!!)
        })
         viewModel.weatherLiveData.observe(viewLifecycleOwner, Observer { weatherData ->
            recyclerviewAdapter.loadWeatherData(weatherData)

        })
        binding?.weatherHistoryDataRecyclerView?.adapter = recyclerviewAdapter
    }
}