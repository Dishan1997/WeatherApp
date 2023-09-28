package com.example.weatherApp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.example.weatherApp.ConstantKeys
import com.example.weatherApp.viewmodels.SearchLocationViewModel
import com.example.getlocation.databinding.SearchLocationBinding
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SearchLocationActivity : AppCompatActivity() {
    private lateinit var binding: SearchLocationBinding
    private lateinit var viewModel: SearchLocationViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SearchLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(SearchLocationViewModel::class.java)

        CoroutineScope(Dispatchers.IO).launch {
            val intent = PlaceAutocomplete.IntentBuilder()
                .accessToken(ConstantKeys.accessToken)
                .placeOptions(null)
                .build(this@SearchLocationActivity)
            startActivityForResult(intent, 111)

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        viewModel.getDataFromLocationSearch(requestCode, resultCode, data)
        val locationData = viewModel.res

        var intent = Intent()
        intent.putExtra("lat", locationData.lat)
        intent.putExtra("long", locationData.long)
        intent.putExtra("cityName", locationData.cityName)
        setResult(300, intent)
        finish()
    }


}