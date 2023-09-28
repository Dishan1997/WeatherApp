package com.example.weatherApp.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weatherApp.apiResponseDataClasses.HourlyWeatherInfoResponse
import com.example.getlocation.databinding.DisplayWeatherHistoryBinding
import java.text.SimpleDateFormat

class ThirdActivityAdapter() : RecyclerView.Adapter<TemperatureViewHolder>() {

    var tempHistoryList: List<HourlyWeatherInfoResponse> = listOf()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TemperatureViewHolder {
        return TemperatureViewHolder(
            DisplayWeatherHistoryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }
    override fun getItemCount(): Int {
        return tempHistoryList.size
    }
    override fun onBindViewHolder(holder: TemperatureViewHolder, position: Int) {
        val temperatureHistory = tempHistoryList[position]
        holder.bind(temperatureHistory)
    }
    fun initWeather(newList: List<HourlyWeatherInfoResponse>) {
        tempHistoryList = newList
        notifyDataSetChanged()
    }
}
class TemperatureViewHolder(val binding: DisplayWeatherHistoryBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(temp: HourlyWeatherInfoResponse) {
        val inputDate = temp.dt_txt
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val outputFormat = SimpleDateFormat("MMM d")
        val outputDate = inputFormat.parse(inputDate)
        val date =  outputFormat.format(outputDate)
        binding.dateTextView.text = date

        val maxTemp1 = temp.main.temp_max - 273.15
         val maxTemp = maxTemp1.toInt()
        val minTemp1 = temp.main.temp_min - 273.15
        val minTemp = minTemp1.toInt()
        binding.maxTemperatureTextView.text = maxTemp.toString() + "ºC/"
        binding.minTemperaturetextView.text = minTemp.toString() + "ºC"

        val dateandTimeValue = temp.dt_txt
        val sdfInput = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val dateAndTime = sdfInput.parse(dateandTimeValue)
        val sdfOutput = SimpleDateFormat("hh:mm a")
        val timeString = sdfOutput.format(dateAndTime)
        binding.timeTextView.text = timeString


        binding.typeWeatherTextView.text = temp.weather[0].main
        val icon = temp.weather[0].icon
        var uri = Uri.parse("https://openweathermap.org/img/w/" + icon + ".png")
        Glide.with(binding.root).load(uri).into(binding.weatherIconImageView)

    }
}