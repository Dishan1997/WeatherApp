package com.example.weatherApp

import io.realm.RealmObject

open class HourlyWeatherInfo : RealmObject(){

    var time: String = ""
    var icon: String = ""
    var temperature: Double = 0.0
    var windSpeed: Double = 0.0
}