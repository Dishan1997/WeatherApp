package com.example.weatherApp.activities


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.getlocation.R
import com.example.getlocation.databinding.ActivityMainBinding
import com.example.weatherApp.fragments.WeatherInfoFragment


class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val fragment = WeatherInfoFragment()
        loadFragment(fragment)
    }
 fun loadFragment(fragment : Fragment){
     val transaction = supportFragmentManager.beginTransaction()
     transaction.replace(R.id.frameLayout, fragment)
     transaction.commit()
 }
}