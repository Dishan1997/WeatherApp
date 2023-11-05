package com.example.weatherApp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.example.weatherApp.ConstantKeys
import com.example.weatherApp.viewmodels.SearchLocationViewModel
import com.example.getlocation.databinding.SearchLocationBinding
import com.example.weatherApp.viewModelFactory.SearchLocationViewModelFactory
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete

class SearchLocationActivity : AppCompatActivity() {
    private lateinit var binding: SearchLocationBinding
    private lateinit var viewModel: SearchLocationViewModel
    companion object{
       const val requestCode = 111
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SearchLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this, SearchLocationViewModelFactory()).get(SearchLocationViewModel::class.java)

        val intent = PlaceAutocomplete.IntentBuilder()
            .accessToken(ConstantKeys.ACCESS_TOKEN)
            .placeOptions(null)
            .build(this@SearchLocationActivity)
        startActivityForResult(intent, requestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        viewModel.getDataFromLocationSearch(requestCode, resultCode, data)
        val locationData = viewModel.getDataFromLocationSearch(requestCode, resultCode, data)

        var intent = Intent()
        intent.putExtra(ConstantKeys.KEY_LATITUDE, locationData.lat)
        intent.putExtra(ConstantKeys.KEY_LONGITUDE, locationData.long)
        intent.putExtra(ConstantKeys.KEY_CITY_NAME, locationData.cityName)
        setResult(WeatherInfoActivity.requestCode, intent)
        finish()
    }

}