package com.example.weatherApp

import android.net.Uri

class WeatherInfo(

    var main: String,
    var icon: Uri,
    var temperature: Double = 0.0

)