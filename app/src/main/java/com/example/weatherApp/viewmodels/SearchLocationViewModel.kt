package com.example.weatherApp.viewmodels

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.weatherApp.LocationSearchData
import com.example.weatherApp.activities.SearchLocationActivity
import com.example.weatherApp.repository.LocationSearchRepository
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete
import org.json.JSONObject

class SearchLocationViewModel( private var repository : LocationSearchRepository) : ViewModel() {

    fun getDataFromLocationSearch(requestCode: Int, resultCode: Int, data: Intent?) : LocationSearchData {
        return repository.getDataFromLocationSearch(requestCode, resultCode,data)
    }

}