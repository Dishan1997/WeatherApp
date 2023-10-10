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
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.getlocation.R
import com.example.getlocation.databinding.WeatherInfoBinding
import com.example.weatherApp.activities.SearchLocationActivity
import com.example.weatherApp.adapter.WeatherInfoActivityAdapter
import com.example.weatherApp.viewmodels.WeatherInfoViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.Locale

class WeatherInfoFragment : Fragment(), LocationListener {
    private var _binding: WeatherInfoBinding? = null
    private val binding get() = _binding!!
    private val locationPermissionCode = 111
    private lateinit var recyclerviewAdapter: WeatherInfoActivityAdapter

    private lateinit var locationManager: LocationManager
    private lateinit var viewModel: WeatherInfoViewModel

    private var cityName = ""
    private var latitude = 0.0
    private var longitude = 0.0
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding= WeatherInfoBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requestLocationPermission()
        binding.weatherDataRecyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recyclerviewAdapter = WeatherInfoActivityAdapter()
        viewModel = ViewModelProvider(this).get(WeatherInfoViewModel::class.java)

        viewModel.weatherInfoLiveData.observe(viewLifecycleOwner, Observer {
            binding.cityNameTextView
            binding.weatherTypeTextView.text = it.main
            binding.temperatureTextView.text = it.temperature.toString() + "ºC"
            Glide.with(this).load(it.icon).into(binding.weatherImageView)
        })

        binding.weatherDataRecyclerView.adapter = recyclerviewAdapter
        viewModel.listOfWeatherInfoLiveData.observe(viewLifecycleOwner, Observer { list ->
            recyclerviewAdapter.loadCurrentWeatherInfo(list)
        })

        binding.weatherDataRecyclerView.adapter = recyclerviewAdapter


        binding.searchCityButton.setOnClickListener {
           var intent = Intent(activity, SearchLocationActivity::class.java)
            startActivityForResult(intent, 300)
        }

        binding.forecastButton.setOnClickListener {
            val bundle = Bundle()
            bundle.putDouble("latitude", latitude)
            bundle.putDouble("longitude",longitude)
            bundle.putString("cityName", cityName)
            val weatherDataForecastFragment = WeatherDataForecastFragment()
            weatherDataForecastFragment.arguments= bundle
            val transaction = fragmentManager?.beginTransaction()
            transaction?.replace(R.id.frameLayout, weatherDataForecastFragment)
            transaction?.addToBackStack(null)
            transaction?.commit()
        }
        setFragmentResultListener("fragmentKey"){key, bundle->
            val cityname = bundle.getString("getCityNameFromFragment")
            binding.cityNameTextView.text = cityname
        }
        requestLocation()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 300 && data != null) {
            getValuesFromSearchLocation(data)
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
        if (requestCode == locationPermissionCode) {
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
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                )
            } != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                locationPermissionCode
            )
        }
    }

    private fun getValuesFromSearchLocation(intent: Intent) {
        var lat = intent.getDoubleExtra("latKey", 0.0)
        var long = intent.getDoubleExtra("longKey", 0.0)
        latitude = lat
        longitude = long

        getApiDataFromViewModel()
        val city = intent.getStringExtra("cityNameKey")
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