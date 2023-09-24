package com.example.getlocation

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.MainThread
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.getlocation.databinding.DesignLayoutBinding

class MainActivityAdapter() :RecyclerView.Adapter<MyViewHolder>(){

    private var tempList : List<HourlyWeatherInfo> = listOf()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return  MyViewHolder(DesignLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        //return MyViewHolder(temperatureList)
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

    fun initTemperature(newTempList: List<HourlyWeatherInfo>){
       tempList = newTempList
        notifyDataSetChanged()
    }

}

class MyViewHolder(val binding: DesignLayoutBinding): RecyclerView.ViewHolder(binding.root){
    fun bind(data: HourlyWeatherInfo){
        binding.textViewTime.text  = data.dt_txt

        val temperatureValue1 = (data.main.temp- 273.15).toString()
        val temperatureValue = String.format("%.2f", temperatureValue1)
        binding.textViewTemp.text  =  temperatureValue + "ºC"

        val icon = data.weather[0].icon
        var uri = Uri.parse("https://openweathermap.org/img/w/" + icon + ".png")
        Glide.with(binding.root).load(uri).into(binding.imageView)

        binding.textViewWind.text = data.wind.speed.toString()


        Log.i("mytag", "${data}")
    }
}
