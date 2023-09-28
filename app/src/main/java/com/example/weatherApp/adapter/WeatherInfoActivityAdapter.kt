package com.example.weatherApp.adapter

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weatherApp.apiResponseDataClasses.HourlyWeatherInfoResponse
import com.example.getlocation.databinding.DisplayWeatherInfoBinding
import java.text.SimpleDateFormat

class MainActivityAdapter() : RecyclerView.Adapter<MyViewHolder>() {

    private var tempList: List<HourlyWeatherInfoResponse> = listOf()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            DisplayWeatherInfoBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun getItemCount(): Int {
        Log.i("mytag", "${tempList.size}")
        return tempList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val temperature = tempList[position]
        holder.bind(temperature)
        Log.i("mytag", "${position}")
    }

    fun initTemperature(newTempList: List<HourlyWeatherInfoResponse>) {
        tempList = newTempList
        notifyDataSetChanged()
    }
}

class MyViewHolder(val binding: DisplayWeatherInfoBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(data: HourlyWeatherInfoResponse) {

        val dateTimeString = data.dt_txt
        val sdfInput = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val date = sdfInput.parse(dateTimeString)
        val sdfOutput = SimpleDateFormat("hh:mm a")
        val timeString = sdfOutput.format(date)
        binding.timeTextView.text = timeString

        val temperatureValue1 = data.main.temp - 273.15
        val temperatureValue = String.format("%.2f", temperatureValue1)
        binding.temperatureTextView.text = temperatureValue + "ºC"

        val icon = data.weather[0].icon
        var uri = Uri.parse("https://openweathermap.org/img/w/" + icon + ".png")
        Glide.with(binding.root).load(uri).into(binding.weatherIconImageView)

        binding.windTextView.text = data.wind.speed.toString() + "Km/h"

        Log.i("mytag", "data = ${data}")
    }
}
