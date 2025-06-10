package com.beny.drinkwaterreminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import java.util.*

class WaterReminderReceiver : BroadcastReceiver() {
    private var mediaPlayer: MediaPlayer? = null
    private var vibrator: Vibrator? = null
    
    companion object {
        private var currentAlarmId: Int = -1
        private var currentMediaPlayer: MediaPlayer? = null
        private var currentVibrator: Vibrator? = null
        
        fun stopCurrentAlarm() {
            currentMediaPlayer?.apply {
                if (isPlaying) {
                    stop()
                }
                release()
            }
            currentMediaPlayer = null
            
            currentVibrator?.cancel()
            currentVibrator = null
            
            currentAlarmId = -1
        }
    }
    
    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let { ctx ->
            if (intent?.action == "DRINK_WATER_ACTION") {
                // Handle the drink water action
                val reminderId = intent.getIntExtra("REMINDER_ID", -1)
                
                // Stop the alarm
                stopCurrentAlarm()
                
                // Cancel the notification
                NotificationManagerCompat.from(ctx).cancel(reminderId)
                
                // Show a confirmation toast
                Toast.makeText(ctx, "Great job staying hydrated!", Toast.LENGTH_SHORT).show()
                return
            }
            
            val reminderId = intent?.getIntExtra("REMINDER_ID", -1) ?: -1
            val hour = intent?.getIntExtra("REMINDER_HOUR", -1) ?: -1
            val minute = intent?.getIntExtra("REMINDER_MINUTE", -1) ?: -1
            
            currentAlarmId = reminderId
            
            // Play alarm sound
            playAlarmSound(ctx)
            
            // Vibrate
            vibrate(ctx)
            
            // Show notification
            showNotification(ctx, reminderId)
            
            // Show toast
            Toast.makeText(ctx, "Time to drink water!", Toast.LENGTH_LONG).show()
            
            // Schedule next alarm for tomorrow
            scheduleNextAlarm(ctx, reminderId, hour, minute)
            
            // Stop alarm sound after 30 seconds
            Handler(Looper.getMainLooper()).postDelayed({
                stopCurrentAlarm()
            }, 30000) // 30 seconds
        }
    }
    
    private fun playAlarmSound(context: Context) {
        try {
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .build()
                )
                setDataSource(
                    context,
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                )
                prepare()
                isLooping = true
                start()
            }
            currentMediaPlayer = mediaPlayer
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun vibrate(context: Context) {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create a vibration pattern: vibrate for 1 second, pause for 0.5 seconds, repeat 3 times
            val pattern = longArrayOf(0, 1000, 500, 1000, 500, 1000)
            val amplitudes = intArrayOf(0, VibrationEffect.DEFAULT_AMPLITUDE, 0, VibrationEffect.DEFAULT_AMPLITUDE, 0, VibrationEffect.DEFAULT_AMPLITUDE)
            vibrator.vibrate(VibrationEffect.createWaveform(pattern, amplitudes, -1))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(longArrayOf(0, 1000, 500, 1000, 500, 1000), -1)
        }
        
        this.vibrator = vibrator
        currentVibrator = vibrator
    }
    
    private fun showNotification(context: Context, reminderId: Int) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("FROM_NOTIFICATION", true)
            putExtra("REMINDER_ID", reminderId)
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            reminderId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Create an action button intent
        val drinkActionIntent = Intent(context, WaterReminderReceiver::class.java).apply {
            action = "DRINK_WATER_ACTION"
            putExtra("REMINDER_ID", reminderId)
        }
        
        val drinkActionPendingIntent = PendingIntent.getBroadcast(
            context,
            reminderId + 1000, // Use a different request code to avoid conflicts
            drinkActionIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val builder = NotificationCompat.Builder(context, MainActivity.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_baseline_local_drink_24)
            .setContentTitle("Time to Drink Water!")
            .setContentText("Stay hydrated - drink some water now")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setFullScreenIntent(pendingIntent, true)
            .addAction(R.drawable.ic_baseline_local_drink_24, "Drink Water", drinkActionPendingIntent)
        
        try {
            NotificationManagerCompat.from(context).notify(reminderId, builder.build())
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }
    
    private fun scheduleNextAlarm(context: Context, reminderId: Int, hour: Int, minute: Int) {
        if (hour == -1 || minute == -1) return
        
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, WaterReminderReceiver::class.java).apply {
            putExtra("REMINDER_ID", reminderId)
            putExtra("REMINDER_HOUR", hour)
            putExtra("REMINDER_MINUTE", minute)
        }
        
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminderId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        // Set for tomorrow at the same time
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            add(Calendar.DATE, 1) // Add one day
        }
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && alarmManager.canScheduleExactAlarms()) {
            alarmManager.setAlarmClock(
                AlarmManager.AlarmClockInfo(calendar.timeInMillis, pendingIntent),
                pendingIntent
            )
        } else {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        }
    }
} 