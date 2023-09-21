package com.example.getlocation

import android.Manifest
import android.app.Activity
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Point
import android.location.Geocoder
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.getlocation.databinding.ActivityMainBinding
import com.example.getlocation.databinding.ActivitySecondBinding
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.Locale

class SecondActivity : AppCompatActivity() {
    private lateinit var binding : ActivitySecondBinding
    private lateinit var pendingIntent : PendingIntent
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySecondBinding.inflate(layoutInflater)
        setContentView(binding.root)


      /*  Mapbox.getInstance(
            this@SecondActivity,
            "sk.eyJ1IjoicmVmYXQwMDEiLCJhIjoiY2xtOG10b3FpMDcxeTNkbzUzcHpuOTlqZCJ9.t6bgJyUUSW0tKMAndNhy9A"
        )
        */

        CoroutineScope(Dispatchers.IO).launch {
            val intent = PlaceAutocomplete.IntentBuilder()
                .accessToken("sk.eyJ1IjoicmVmYXQwMDEiLCJhIjoiY2xtOG10b3FpMDcxeTNkbzUzcHpuOTlqZCJ9.t6bgJyUUSW0tKMAndNhy9A")
                .placeOptions(null)
                .build(this@SecondActivity)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                pendingIntent = PendingIntent.getBroadcast(
                    this@SecondActivity,
                    111,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
                )

                startActivityForResult(intent, 111)
                //  pendingIntent.send(this, 111, intent)
            } else {
                PendingIntent.getBroadcast(
                    this@SecondActivity,
                    111,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
                startActivityForResult(intent, 111)
                //  pendingIntent.send(this, 111, intent)
            }
            //  startActivityForResult(intent, 111)

        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        if(resultCode == Activity.RESULT_OK && requestCode == 111){
            val feature = PlaceAutocomplete.getPlace(data)
          //  var cityName = feature.placeName()

            Log.i("mytag", feature.toString())

            val jsonString = feature.toJson()

            val jsonObject = JSONObject(jsonString)
            val geo = jsonObject.getJSONObject("geometry")
            val point = geo.getJSONArray("coordinates")
           var cityName = ""
            try{
                val context = jsonObject.getJSONArray("context")
                val carmenContext = context[1] as JSONObject
                 cityName = carmenContext.getString("text")
            }catch(exception : Exception){
                Log.i("mytag", exception.message.toString())
            }

            val long : Double = point.get(0) as Double
            val lat : Double = point.get(1) as Double

            var intent = Intent()
            intent.putExtra("lat", lat)
            intent.putExtra("long", long)
            intent.putExtra("cityName", cityName)
            setResult(300, intent)
            finish()

        }
    }


}