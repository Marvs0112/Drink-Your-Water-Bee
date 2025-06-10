package com.beny.drinkwaterreminder

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson

class ReminderAdapter(private val context: Context) : 
    RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder>() {
    
    class ReminderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtTime: TextView = view.findViewById(R.id.txtTime)
        val delBtn: Button = view.findViewById(R.id.delBtn)
        val editBtn: Button = view.findViewById(R.id.editBtn)
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.reminder_row, parent, false)
        return ReminderViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: ReminderViewHolder, position: Int) {
        val reminder = NotificationReminder.times[position]
        holder.txtTime.text = reminder.time
        
        holder.delBtn.setOnClickListener {
            // Cancel the alarm
            (context as? NotificationReminder)?.cancelAlarm(reminder.id)
            
            // Remove from list and update UI
            NotificationReminder.times.removeAt(position)
            notifyItemRemoved(position)
            notifyDataSetChanged()
            saveData()
            
            Toast.makeText(context, "Reminder deleted", Toast.LENGTH_SHORT).show()
        }
        
        holder.editBtn.setOnClickListener {
            // TODO: Implement edit functionality
            Toast.makeText(context, "Edit functionality coming soon", Toast.LENGTH_SHORT).show()
        }
    }
    
    override fun getItemCount(): Int = NotificationReminder.times.size
    
    private fun saveData() {
        context.getSharedPreferences("reminder", Context.MODE_PRIVATE)
            .edit()
            .putString("times", Gson().toJson(NotificationReminder.times))
            .apply()
    }
    
    private fun hourExtracter(time: String): Int {
        return try {
            time.split(":")[0].toInt()
        } catch (e: Exception) {
            0
        }
    }
    
    private fun minuteExtracter(time: String): Int {
        return try {
            time.split(":")[1].toInt()
        } catch (e: Exception) {
            0
        }
    }
} 