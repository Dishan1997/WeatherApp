package com.example.getlocation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.getlocation.databinding.ActivityThirdBinding
import com.example.getlocation.databinding.DesignSavehistoryBinding
import com.example.getlocation.databinding.DesignLayoutBinding
import io.realm.Realm

class ThirdActivityAdapter (var tempHistoryList : List<Temperature>) : RecyclerView.Adapter<TemperatureViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TemperatureViewHolder {
      return  TemperatureViewHolder(DesignSavehistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
      return tempHistoryList.size
    }

    override fun onBindViewHolder(holder: TemperatureViewHolder, position: Int) {
        val temperatureHistory = tempHistoryList[position]
        holder.bind(temperatureHistory)
    }

}

class TemperatureViewHolder(binding : DesignSavehistoryBinding) : RecyclerView.ViewHolder(binding.root){

  fun bind(temp: Temperature){
//      binding.textViewHistoryTime.text  = temp.list[2].weather[0].main
//      binding.textViewHistoryTime.text  = temp.list[1].main.temp.toString()
//      binding.imageView.setImageResource(temp.img)
//      binding.textViewHistoryWind.text = temp.list[4].wind.speed.toString()

  }
}