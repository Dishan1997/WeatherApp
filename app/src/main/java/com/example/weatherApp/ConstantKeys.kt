package com.example.weatherApp

class ConstantKeys {
    companion object {
        const val accessToken: String =
            "sk.eyJ1IjoicmVmYXQwMDEiLCJhIjoiY2xtOG10b3FpMDcxeTNkbzUzcHpuOTlqZCJ9.t6bgJyUUSW0tKMAndNhy9A"
        const val appid: String = "80c089997f86207df0e810e2f184982a"
        const val BASE_URLforCurrentData : String= "https://api.openweathermap.org/data/2.5/weather?"
        const val BASE_URLforHourlyData : String = "https://api.openweathermap.org/data/2.5/forecast?"
        const val ICON_URL : String = "https://openweathermap.org/img/w/"
    }
}