package com.beny.drinkwaterreminder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.beny.drinkwaterreminder.databinding.ItemTipBinding

class TipsAdapter(private val tips: kotlin.collections.List<String>) : 
    RecyclerView.Adapter<TipsAdapter.TipViewHolder>() {
    
    class TipViewHolder(private val binding: ItemTipBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(tip: String) {
            binding.tipText.text = tip
        }
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TipViewHolder {
        val binding = ItemTipBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TipViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: TipViewHolder, position: Int) {
        holder.bind(tips[position])
    }
    
    override fun getItemCount(): Int = tips.size
} 