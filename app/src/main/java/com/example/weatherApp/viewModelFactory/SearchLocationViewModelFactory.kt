package com.example.weatherApp.viewModelFactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherApp.repository.LocationSearchRepository
import com.example.weatherApp.viewmodels.SearchLocationViewModel

class SearchLocationViewModelFactory() : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val repository = LocationSearchRepository()
        return SearchLocationViewModel(repository) as T
    }
}