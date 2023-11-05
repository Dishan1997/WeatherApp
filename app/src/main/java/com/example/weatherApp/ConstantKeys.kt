package com.example.weatherApp

class ConstantKeys {
    companion object {
        const val ACCESS_TOKEN: String = "sk.eyJ1IjoicmVmYXQwMDEiLCJhIjoiY2xtOG10b3FpMDcxeTNkbzUzcHpuOTlqZCJ9.t6bgJyUUSW0tKMAndNhy9A"
        const val APP_ID: String = "80c089997f86207df0e810e2f184982a"
        const val BASE_URL : String= "https://api.openweathermap.org/"
        const val ICON_URL : String = "https://openweathermap.org/img/w/"
        const val KEY_LATITUDE : String = "latitude"
        const val KEY_LONGITUDE : String = "longitude"
        const val KEY_CITY_NAME : String = "cityName"
        const val LOCATION_PERMISSION_CODE = 111
        const val DATE_PATTERN = "yyyy-MM-dd HH:mm:ss"
        const val DAY_PATTERN = "MMM d"
        const val KELVIN_TO_CELCIUS_VALUE = 273.15
    }
}