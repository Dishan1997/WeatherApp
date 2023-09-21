package com.example.getlocation

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class TemperatureDatabase : RealmObject() {
    @PrimaryKey
    var id : Int = 0
    var temperature : Double = 0.0
    var time : String = ""
    var icon : String = ""
    var wind : String = ""
}