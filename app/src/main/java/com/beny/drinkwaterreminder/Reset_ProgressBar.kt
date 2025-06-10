package com.beny.drinkwaterreminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences

class Reset_ProgressBar : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val progressState: SharedPreferences = context.getSharedPreferences("progress", Context.MODE_PRIVATE)
        val editor = progressState.edit()
        editor.putString("progressState", "0")
        editor.apply()
    }
} 