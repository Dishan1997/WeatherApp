package com.example.weatherApp.realm

import io.realm.DynamicRealm
import io.realm.RealmMigration

class MyRealmMigration : RealmMigration {
    override fun migrate(realm: DynamicRealm, oldVersion: Long, newVersion: Long) {
        val schema = realm.schema

        if (oldVersion == 0L && newVersion == 1L) {
            val currentWeatherInfoClass = schema.get("CurrentWeatherInfo")
            currentWeatherInfoClass?.addField("newField", currentWeatherInfoClass::class.java)
        }
        if (oldVersion == 0L && newVersion == 1L) {
            val currentWeatherInfoClass = schema.get("HourlyWeatherInfo")
            currentWeatherInfoClass?.addField("newField", HourlyWeatherInfo::class.java)
        }
        if (oldVersion == 0L && newVersion == 1L) {
            val currentWeatherInfoClass = schema.get("WeatherForecastInfo")
            currentWeatherInfoClass?.addField("newField", WeatherForecastInfo::class.java)
        }

    }
}