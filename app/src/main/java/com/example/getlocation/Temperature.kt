package com.example.getlocation

data class Temperature(
    //var id : Int =0,
    var list : List<ListofData> = listOf()
)

data class ListofData(
    val main : TemperatureValue,
    val weather : List<Weather>,
    val wind : Wind,
    val dt_txt : String
)

data class TemperatureValue(
    val temp : Double
)
data class Weather(
    val main: String,
    val icon : Int
)
data class Wind(
    val speed : Double
)