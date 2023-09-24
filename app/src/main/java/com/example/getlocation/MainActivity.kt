package com.example.getlocation

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import com.example.getlocation.databinding.ActivityMainBinding
import java.util.Locale
import android.location.LocationListener
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity(), LocationListener {

    private lateinit var binding: ActivityMainBinding
    private val locationPermissionCode = 111
    private lateinit var recyclerviewAdapter: MainActivityAdapter

    private lateinit var locationManager: LocationManager
    private lateinit var viewModel: MainActivityViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //current Location track
        getLocation()

        // viewModel Starts -----------------------------------
        viewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)
        viewModel.weatherInfoLiveData.observe(this, Observer {
            binding.textViewWeatherType.text = it.main
            binding.textViewTemperature.text = it.temperature.toString() + "ºC"
            Glide.with(this).load(it.icon).into(binding.imageViewWeather)
        })

        // viewModel end -----------------------------------


        // recyclerView Starts -----------------------------------

        binding.recyclerViewWeatherData.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
         recyclerviewAdapter = MainActivityAdapter()

        viewModel.listOfWeatherInfoLiveData.observe(this, Observer { list ->
            recyclerviewAdapter.initTemperature(list)
        })
        binding.recyclerViewWeatherData.adapter = recyclerviewAdapter

        // recyclerView ends -----------------------------------


        //second activity
        binding.buttonSearchCity.setOnClickListener {
            var intent = Intent(this, SecondActivity::class.java)
            startActivityForResult(intent, 300)
        }

        requestLocation()

        // third activity
        binding.buttonHistory.setOnClickListener {
            var intent = Intent(this, ThirdActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 300 && data != null) {
            getValuesFromSecondActivity(data)
        }
        Log.i("mytag", "inside onActivityResult")
    }


    override fun onLocationChanged(location: Location) {
        val city = getCityName(location.latitude, location.longitude)
        Log.i("mytag", "Activity1 onLocationChanges: " + city)
        binding.textViewCityName.text = city
        //get weather data
        GlobalScope.launch {
            viewModel.fetchWeatherInfo(location.latitude, location.longitude)
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == locationPermissionCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestLocation()
            }
        }
        Log.i("mytag", "Activity1 : onRequestPermissionsResult")
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


    // current Location track Start -----------------------------------
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
                locationPermissionCode
            )
        }
        Log.i("mytag", "inside getLocation")
    }

    private fun getValuesFromSecondActivity(intent: Intent) {

        var lat = intent.getDoubleExtra("lat", 0.0)
        var long = intent.getDoubleExtra("long", 0.0)

        GlobalScope.launch {
            viewModel.fetchWeatherInfoHourly(lat, long)
        }

        Log.i("mytag", "lat=" + lat.toString() + " long=" + long.toString())
        //val cityName =  getCityName(lat, long)
        val cityName = intent.getStringExtra("cityName")
        binding.textViewCityName.text = cityName
    }

    private fun getCityName(lat: Double, long: Double): String {
        var cityName: String = ""
        var geoCoder = Geocoder(this, Locale.getDefault())
        var addresses = geoCoder.getFromLocation(lat, long, 3)
        if (addresses.isNullOrEmpty()) {
            return ""
        }

        var address = addresses.get(0)
        cityName = address.locality.toString()
        return cityName
    }
    // current Location track ends -----------------------------------


}