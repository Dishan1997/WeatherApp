package com.example.weatherApp.repository

import android.app.Activity
import android.content.Intent
import com.example.weatherApp.LocationSearchData
import com.example.weatherApp.activities.SearchLocationActivity
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete
import org.json.JSONObject

class LocationSearchRepository {
    fun getDataFromLocationSearch(requestCode: Int, resultCode: Int, data: Intent?) : LocationSearchData {
        var res = LocationSearchData(0.0, 0.0, "")
        if (resultCode == Activity.RESULT_OK && requestCode == SearchLocationActivity.requestCode) {
            val feature = PlaceAutocomplete.getPlace(data)
            val jsonString = feature.toJson()
            val jsonObject = JSONObject(jsonString)
            val geo = jsonObject.getJSONObject("geometry")
            val point = geo.getJSONArray("coordinates")
            var cityName = ""

            if(jsonObject != null && point!= null) {
                val context = jsonObject.getJSONArray("context")
                val carmenContext = context[1] as JSONObject
                cityName = carmenContext.getString("text")
                val long: Double = point.get(0) as Double
                val lat: Double = point.get(1) as Double
                res = LocationSearchData(lat, long, cityName)

            }
        }
        return res
    }

}