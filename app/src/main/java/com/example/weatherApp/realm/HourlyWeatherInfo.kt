package com.example.weatherApp.realm

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class HourlyWeatherInfo : RealmObject() {
    var date: String = ""
    var time: String = ""
    var icon: String = ""
    var temperatureType: String = ""
    var minTemperature: Double = 0.0
    var maxTemperature: Double = 0.0
    var temperature: Double = 0.0
    var windSpeed: Double = 0.0

}