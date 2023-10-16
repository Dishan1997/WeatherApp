package com.example.weatherApp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.getlocation.databinding.DisplayWeatherForecastBinding
import com.example.weatherApp.ConstantKeys
import com.example.weatherApp.apiResponse.HourlyWeatherInfoResponse

import java.text.SimpleDateFormat

class WeatherDataForecastActivityAdapter() : RecyclerView.Adapter<TemperatureViewHolder>() {

    private var weatherHistoryList: List<HourlyWeatherInfoResponse> = listOf()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TemperatureViewHolder {
        return TemperatureViewHolder(
            DisplayWeatherForecastBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }
    override fun getItemCount(): Int {
        return weatherHistoryList.size
    }
    override fun onBindViewHolder(holder: TemperatureViewHolder, position: Int) {
        val temperatureHistory = weatherHistoryList[position]
        holder.bind(temperatureHistory)
    }
    fun loadWeatherData(newList: List<HourlyWeatherInfoResponse>) {
        weatherHistoryList = newList
        notifyDataSetChanged()
    }
}
class TemperatureViewHolder(val binding: DisplayWeatherForecastBinding) :
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
        var url = "${ConstantKeys.ICON_URL}" + icon + ".png"
        Glide.with(binding.root).load(url).into(binding.weatherIconImageView)

    }
}