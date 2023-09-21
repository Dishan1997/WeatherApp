package com.example.getlocation

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.getlocation.databinding.DesignLayoutBinding

class MainActivityAdapter(var tempList : List<ListofData>) :RecyclerView.Adapter<MyViewHolder>(){


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


}

class MyViewHolder(val binding: DesignLayoutBinding): RecyclerView.ViewHolder(binding.root){
    fun bind(data: ListofData){
        binding.textViewTime.text  = data.dt_txt
        binding.textViewTemp.text  = data.main.temp.toString()
        binding.imageView.setImageResource(data.weather[0].icon)
        binding.textViewWind.text = data.wind.toString()


//        binding.textViewTime.text  = data.name.toString()
//        binding.textViewTemp.text  = data.age.toString()
//        binding.textViewWind.text =  data.id.toString()

        Log.i("mytag", "${data}")
    }
}
