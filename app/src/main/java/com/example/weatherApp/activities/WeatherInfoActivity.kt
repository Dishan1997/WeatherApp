package com.example.weatherApp.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import java.util.Locale
import android.location.LocationListener
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.weatherApp.adapter.WeatherInfoActivityAdapter
import com.example.weatherApp.viewmodels.WeatherInfoViewModel
import com.example.getlocation.databinding.WeatherInfoBinding
import com.example.weatherApp.ConstantKeys
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class WeatherInfoActivity : AppCompatActivity(), LocationListener {

    private lateinit var binding: WeatherInfoBinding
    private var recyclerviewAdapter= WeatherInfoActivityAdapter()

    private lateinit var locationManager: LocationManager
    private lateinit var viewModel: WeatherInfoViewModel

    private var cityName = ""
    private var latitude = 0.0
    private var longitude = 0.0

    companion object
    {
        const val requestCode = 300
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = WeatherInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getLocation()

        binding.weatherDataRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerviewAdapter = WeatherInfoActivityAdapter()

        viewModel = ViewModelProvider(this).get(WeatherInfoViewModel::class.java)

        viewModel.weatherInfoLiveData.observe(this, Observer {
            binding.weatherTypeTextView.text = it.main
            binding.temperatureTextView.text = it.temperature.toString() + "ºC"
            Glide.with(this).load(it.icon).into(binding.weatherImageView)
        })


        binding.weatherDataRecyclerView.adapter = recyclerviewAdapter
        viewModel.listOfWeatherInfoLiveData.observe(this, Observer { list ->
            recyclerviewAdapter.loadCurrentWeatherInfo(list)
        })

        binding.searchCityButton.setOnClickListener {
            var intent = Intent(this, SearchLocationActivity::class.java)
            startActivityForResult(intent, requestCode)
        }

        binding.forecastButton.setOnClickListener {
            var intent = Intent(this, WeatherDataForecastActivity::class.java)
            intent.putExtra(ConstantKeys.KEY_LATITUDE, latitude)
            intent.putExtra(ConstantKeys.KEY_LONGITUDE, longitude)
            intent.putExtra(ConstantKeys.KEY_CITY_NAME, cityName)
            startActivity(intent)
        }

        requestLocation()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == requestCode && data != null) {
            getValuesFromSecondActivity(data)
        }
    }

    override fun onLocationChanged(location: Location) {
        latitude = location.latitude
        longitude = location.longitude
        val city = getCityName(location.latitude, location.longitude)
        binding.cityNameTextView.text = city
        cityName = city

        getApiDataFromViewModel()
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
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 400, 1f, this)
    }

    private fun getLocation() {
        locationManager = applicationContext.getSystemService(LOCATION_SERVICE) as LocationManager
        if ((ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED)
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                ConstantKeys.LOCATION_PERMISSION_CODE
            )
        }
    }

    private fun getValuesFromSecondActivity(intent: Intent) {
        var lat = intent.getDoubleExtra(ConstantKeys.KEY_LATITUDE, 0.0)
        var long = intent.getDoubleExtra(ConstantKeys.KEY_LONGITUDE, 0.0)
        latitude = lat
        longitude = long

        getApiDataFromViewModel()
        val city = intent.getStringExtra(ConstantKeys.KEY_CITY_NAME)
        binding.cityNameTextView.text = city
        cityName = city.toString()
    }

    private fun getApiDataFromViewModel() {
        GlobalScope.launch {
            viewModel.fetchWeatherInfoHourly(latitude, longitude)
            viewModel.fetchWeatherInfo(latitude, longitude)
        }
    }

    private fun getCityName(lat: Double, long: Double): String {

        var city: String = ""
        var geoCoder = Geocoder(this, Locale.getDefault())
        var addresses = geoCoder.getFromLocation(lat, long, 3)
        if (addresses.isNullOrEmpty()) {
            return ""
        }
        var address = addresses.get(0)
        city = address.locality.toString()
        return city
    }

}