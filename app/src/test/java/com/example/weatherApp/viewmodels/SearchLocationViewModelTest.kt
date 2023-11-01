package com.example.weatherApp.viewmodels

import android.content.Intent
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.weatherApp.repository.LocationSearchRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

class SearchLocationViewModelTest{
    private val testDispatcher  = StandardTestDispatcher()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    lateinit var viewmodel: SearchLocationViewModel
    @Mock
     lateinit var repository: LocationSearchRepository

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        viewmodel = SearchLocationViewModel(repository)
        Dispatchers.setMain(testDispatcher)
    }

    @Test
    fun testGetDataFromLocationSearch(){
       // Mockito.`when`(repository.getDataFromLocationSearch(11, 11, 11))

    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
}