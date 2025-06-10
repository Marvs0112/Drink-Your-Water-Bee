package com.beny.drinkwaterreminder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.beny.drinkwaterreminder.databinding.ItemDrinkHistoryBinding
import java.text.SimpleDateFormat
import java.util.*

class DrinkHistoryAdapter(private val items: ArrayList<DrinkedList>) : 
    RecyclerView.Adapter<DrinkHistoryAdapter.ViewHolder>() {

    class ViewHolder(private val binding: ItemDrinkHistoryBinding) : 
        RecyclerView.ViewHolder(binding.root) {
        
        fun bind(item: DrinkedList) {
            try {
                // Parse the original date string
                val inputFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.US)
                val date = inputFormat.parse(item.time)
                
                // Format the date without timezone
                val outputFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
                outputFormat.timeZone = TimeZone.getDefault() // Use device's timezone
                
                binding.apply {
                    timeText.text = outputFormat.format(date ?: Date())
                    amountText.text = "${item.amount} ml"
                }
            } catch (e: Exception) {
                e.printStackTrace()
                binding.apply {
                    timeText.text = item.time
                    amountText.text = "${item.amount} ml"
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemDrinkHistoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size
} 