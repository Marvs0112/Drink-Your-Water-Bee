package com.beny.drinkwaterreminder

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MyAdapter(private val context: Context) : 
    RecyclerView.Adapter<MyAdapter.MyViewHolder>() {
    
    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val arrayListText: TextView = view.findViewById(R.id.arrayListText)
        val deleteBtn: Button = view.findViewById(R.id.deleteBtn)
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.my_row, parent, false)
        return MyViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = DrinkedList.Drinked[position]
        holder.arrayListText.text = item.toString()
        
        holder.deleteBtn.setOnClickListener {
            val amount = DrinkedList.Drinked[position].amount
            DrinkedList.Drinked.removeAt(position)
            notifyItemRemoved(position)
            notifyDataSetChanged()
            
            (context as? DrinkHistoryActivity)?.let { activity ->
                activity.saveData(context)
                activity.transfer_ml(amount)
            }
        }
    }
    
    override fun getItemCount(): Int = DrinkedList.Drinked.size
} 