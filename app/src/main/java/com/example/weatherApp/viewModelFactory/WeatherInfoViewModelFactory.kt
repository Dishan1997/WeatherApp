package com.example.weatherApp.viewModelFactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherApp.dao.WeatherInfoDao
import com.example.weatherApp.repository.WeatherInfoRepository
import com.example.weatherApp.viewmodels.WeatherInfoViewModel

class WeatherInfoViewModelFactory() : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val dao = WeatherInfoDao()
        val repository = WeatherInfoRepository(dao)
        return WeatherInfoViewModel(repository) as T
    }

}