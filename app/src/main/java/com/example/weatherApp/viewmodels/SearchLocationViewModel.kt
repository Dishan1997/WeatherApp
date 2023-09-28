package com.example.weatherApp.viewmodels

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.weatherApp.LocationSearchData
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete
import org.json.JSONObject

class SearchLocationViewModel : ViewModel() {

    lateinit var res: LocationSearchData
    fun getDataFromLocationSearch(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == 111) {
            val feature = PlaceAutocomplete.getPlace(data)
            val jsonString = feature.toJson()
            val jsonObject = JSONObject(jsonString)
            val geo = jsonObject.getJSONObject("geometry")
            val point = geo.getJSONArray("coordinates")
            var cityName = ""

            try {
                val context = jsonObject.getJSONArray("context")
                val carmenContext = context[1] as JSONObject
                cityName = carmenContext.getString("text")
            } catch (exception: Exception) {
                Log.i("mytag", exception.message.toString())
            }

            try {
                val long: Double = point.get(0) as Double
                val lat: Double = point.get(1) as Double
                res = LocationSearchData(lat, long, cityName)

            } catch (e: Exception) {
                Log.i("mytag", e.message.toString())
            }

        }
    }


}