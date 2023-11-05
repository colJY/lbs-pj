package com.example.lbs

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.lbs.common.Location
import com.example.lbs.databinding.ItemLocationBinding

class MainAdapter(private val itemList : ArrayList<Location>) : RecyclerView.Adapter<MainAdapter.ItemHolder>() {

/*    inner class ItemHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        var latitudeValue = itemView.findViewById<TextView>(R.id.latitude)
        var longitudeValue = itemView.findViewById<TextView>(R.id.longitude)
    }*/
    inner class ItemHolder(val binding: ItemLocationBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val binding = ItemLocationBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ItemHolder(binding)
    }

    override fun getItemCount() = itemList.size

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        var location : Location = itemList[position]
        holder.binding.latitude.text = location.latitude.toString()
        holder.binding.longitude.text = location.longitude.toString()
    }
    @SuppressLint("NotifyDataSetChanged")
    fun addData(location: Location){
        itemList.add(location)
        notifyDataSetChanged()
    }
}