package com.example.weatherApp.fragments

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.getlocation.R
import com.example.getlocation.databinding.WeatherInfoBinding
import com.example.weatherApp.ConstantKeys
import com.example.weatherApp.activities.SearchLocationActivity
import com.example.weatherApp.activities.WeatherInfoActivity
import com.example.weatherApp.adapter.WeatherInfoActivityAdapter
import com.example.weatherApp.viewModelFactory.WeatherInfoViewModelFactory
import com.example.weatherApp.viewmodels.WeatherInfoViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.Locale

class WeatherInfoFragment : Fragment(), LocationListener {
    private var binding: WeatherInfoBinding? = null
    private var recyclerviewAdapter= WeatherInfoActivityAdapter()
    private lateinit var locationManager: LocationManager
    private lateinit var viewModel: WeatherInfoViewModel

    private var cityName = ""
    var latitude = 0.0
    var longitude = 0.0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= WeatherInfoBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requestLocationPermission()
        binding?.weatherDataRecyclerView?.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        activity?.let{
            viewModel = ViewModelProvider(it, WeatherInfoViewModelFactory()).get(WeatherInfoViewModel::class.java)
        }

        viewModel.weatherInfoLiveData.observe(viewLifecycleOwner, Observer {
            binding?.cityNameTextView?.text = cityName
            binding?.weatherTypeTextView?.text = it.main
            binding?.temperatureTextView?.text = it.temperature.toString() + "ºC"
            Glide.with(this).load(it.icon).into(binding?.weatherImageView!!)
        })

        binding?.weatherDataRecyclerView?.adapter = recyclerviewAdapter
        viewModel.listOfWeatherInfoLiveData.observe(viewLifecycleOwner, Observer { list ->
            recyclerviewAdapter.loadCurrentWeatherInfo(list)
        })
        binding?.searchCityButton?.setOnClickListener {
           var intent = Intent(activity, SearchLocationActivity::class.java)
            startActivityForResult(intent, WeatherInfoActivity.requestCode)
        }

        binding?.forecastButton?.setOnClickListener {
            val bundle = Bundle()
            bundle.putDouble(ConstantKeys.KEY_LATITUDE, latitude)
            bundle.putDouble(ConstantKeys.KEY_LONGITUDE,longitude)
            bundle.putString(ConstantKeys.KEY_CITY_NAME, cityName)
            val weatherDataForecastFragment = WeatherDataForecastFragment()
            weatherDataForecastFragment.arguments= bundle
            val transaction = fragmentManager?.beginTransaction()
            transaction?.replace(R.id.frameLayout, weatherDataForecastFragment)
            transaction?.addToBackStack(null)
            transaction?.commit()
        }
        requestLocation()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == WeatherInfoActivity.requestCode && data != null) {
            getValuesFromSearchLocation(data)
        }
    }
    override fun onLocationChanged(location: Location) {
        latitude = location.latitude
        longitude = location.longitude
        val city = getCityName(location.latitude, location.longitude)
        binding?.cityNameTextView?.text = city
        cityName = city
        getWeatherDataFromViewModel()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == ConstantKeys.LOCATION_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestLocation()
            }
        }
    }

    private fun requestLocation() {
        if ((context?.let {
                ActivityCompat.checkSelfPermission(
                    it,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            } != PackageManager.PERMISSION_GRANTED) && (context?.let {
                ActivityCompat.checkSelfPermission(
                    it,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            } != PackageManager.PERMISSION_GRANTED)
        ) {
            return
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 400, 1f, this)
    }

    private fun requestLocationPermission() {
        locationManager = activity?.applicationContext?.getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager
        if (context?.let {
                ContextCompat.checkSelfPermission(
                    it,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            } != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                ConstantKeys.LOCATION_PERMISSION_CODE
            )
        }
    }

    private fun getValuesFromSearchLocation(intent: Intent) {
        var lat = intent.getDoubleExtra(ConstantKeys.KEY_LATITUDE, 0.0)
        var long = intent.getDoubleExtra(ConstantKeys.KEY_LONGITUDE, 0.0)
        latitude = lat
        longitude = long

        getWeatherDataFromViewModel()
        val city = intent.getStringExtra(ConstantKeys.KEY_CITY_NAME)
        binding?.cityNameTextView?.text = city
        cityName = city.toString()
    }
    private fun getWeatherDataFromViewModel() {
        GlobalScope.launch {
            viewModel.fetchWeatherInfoHourly(latitude, longitude)
            viewModel.fetchWeatherInfo(latitude, longitude)
        }
    }
    private fun getCityName(lat: Double, long: Double): String {

        var city: String = ""
        var geoCoder = context?.let { Geocoder(it, Locale.getDefault()) }
        var addresses = geoCoder?.getFromLocation(lat, long, 3)
        if (addresses.isNullOrEmpty()) {
            return ""
        }
        var address = addresses.get(0)
        city = address.locality.toString()
        return city
    }
}