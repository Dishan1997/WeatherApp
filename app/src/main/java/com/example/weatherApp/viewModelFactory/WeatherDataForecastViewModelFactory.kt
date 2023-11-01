package com.example.weatherApp.viewModelFactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherApp.dao.WeatherForecastDao
import com.example.weatherApp.repository.WeatherForecastRepository
import com.example.weatherApp.retrofit.WeatherForecastResponseService
import com.example.weatherApp.viewmodels.WeatherDataForecastViewModel

class WeatherDataForecastViewModelFactory() : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val apiService = WeatherForecastResponseService.getInstance()
        val dao = WeatherForecastDao()
        val repository = WeatherForecastRepository(apiService,dao)
        return WeatherDataForecastViewModel(repository) as T
    }
}