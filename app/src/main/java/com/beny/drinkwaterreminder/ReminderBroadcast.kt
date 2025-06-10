package com.beny.drinkwaterreminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import androidx.core.app.NotificationCompat
import java.io.Serializable

class ReminderBroadcast : BroadcastReceiver(), Serializable {
    var id: Int = -1
        private set
    
    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let { ctx ->
            id = intent?.getIntExtra("ID", -1) ?: -1
            Log.d("ReminderBroadcast", "onReceive called with id: $id")
            
            when (id) {
                HomeFragment.RESET_ID -> handleReset(ctx)
                else -> handleReminder(ctx)
            }
        }
    }
    
    private fun handleReset(context: Context) {
        Log.d("ReminderBroadcast", "Handling reset with id: $id")
        context.getSharedPreferences("Reset", Context.MODE_PRIVATE)
            .edit()
            .putBoolean("Reset", true)
            .apply()
    }
    
    private fun handleReminder(context: Context) {
        val notificationUtils = NotificationUtils(context)
        val builder = notificationUtils.setNotification(
            "It's time to drink water!",
            "Confirm that you have just drunk water",
            id
        )
        notificationUtils.getManager().notify(id, builder.build())
    }
    
    companion object {
        private const val serialVersionUID = 1L
    }
} 