package com.beny.drinkwaterreminder

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.TimePicker
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.beny.drinkwaterreminder.databinding.ActivityNotificationBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.Serializable
import java.util.*

class NotificationReminder : AppCompatActivity() {
    private lateinit var binding: ActivityNotificationBinding
    private lateinit var reminderAdapter: ReminderAdapter
    private lateinit var alarmManager: AlarmManager
    private var pendingTimePickerCallback: (() -> Unit)? = null
    
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            pendingTimePickerCallback?.invoke()
        } else {
            showPermissionExplanationDialog()
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        loadData()
        setupRecyclerView()
        setupFloatingActionButton()
        checkPermissions()
    }
    
    private fun checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                showAlarmPermissionDialog()
            }
        }
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
    
    private fun showAlarmPermissionDialog() {
        AlertDialog.Builder(this)
            .setTitle("Alarm Permission Required")
            .setMessage("To set water reminders, this app needs permission to schedule exact alarms. Please enable this in Settings.")
            .setPositiveButton("Open Settings") { _, _ ->
                startActivity(Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM))
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun showPermissionExplanationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Notification Permission Required")
            .setMessage("To receive water reminders, this app needs notification permission. Would you like to grant it in Settings?")
            .setPositiveButton("Open Settings") { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.fromParts("package", packageName, null)
                startActivity(intent)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun setupRecyclerView() {
        reminderAdapter = ReminderAdapter(this)
        binding.ReminderRecycleView.apply {
            adapter = reminderAdapter
            layoutManager = LinearLayoutManager(this@NotificationReminder)
        }
    }
    
    private fun setupFloatingActionButton() {
        binding.floatingActionButton.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
                showAlarmPermissionDialog()
                return@setOnClickListener
            }
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    pendingTimePickerCallback = { showTimePickerDialog() }
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    return@setOnClickListener
                }
            }
            
            showTimePickerDialog()
        }
    }
    
    private fun showTimePickerDialog() {
        val calendar = Calendar.getInstance()
        val timePickerDialog = TimePickerDialog(
            this,
            { _: TimePicker, hourOfDay: Int, minute: Int ->
                val time = String.format("%02d:%02d", hourOfDay, minute)
                val id = Random().nextInt()
                
                scheduleAlarm(hourOfDay, minute, id)
                times.add(ReminderHolder(id, time))
                reminderAdapter.notifyDataSetChanged()
                saveData()
                
                Toast.makeText(this, "Water reminder alarm set for $time", Toast.LENGTH_SHORT).show()
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            android.text.format.DateFormat.is24HourFormat(this)
        )
        timePickerDialog.show()
    }
    
    private fun scheduleAlarm(hourOfDay: Int, minute: Int, id: Int) {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hourOfDay)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            
            // If time has already passed today, set for tomorrow
            if (before(Calendar.getInstance())) {
                add(Calendar.DATE, 1)
            }
        }
        
        val intent = Intent(this, WaterReminderReceiver::class.java).apply {
            putExtra("REMINDER_ID", id)
            putExtra("REMINDER_HOUR", hourOfDay)
            putExtra("REMINDER_MINUTE", minute)
        }
        
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        // Set exact repeating alarm
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
    
    fun cancelAlarm(id: Int) {
        val intent = Intent(this, WaterReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }
    
    private fun loadData() {
        val sharedPreferences = getSharedPreferences("reminder", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString("times", null)
        
        if (json != null) {
            val type = object : TypeToken<ArrayList<ReminderHolder>>() {}.type
            times.clear()
            times.addAll(gson.fromJson(json, type))
            
            // Reschedule all alarms after app restart
            times.forEach { reminder ->
                val timeParts = reminder.time.split(":")
                if (timeParts.size == 2) {
                    val hour = timeParts[0].toInt()
                    val minute = timeParts[1].toInt()
                    scheduleAlarm(hour, minute, reminder.id)
                }
            }
        }
    }
    
    private fun saveData() {
        getSharedPreferences("reminder", Context.MODE_PRIVATE)
            .edit()
            .putString("times", Gson().toJson(times))
            .apply()
    }
    
    data class ReminderHolder(
        val id: Int,
        val time: String
    ) : Serializable {
        companion object {
            private const val serialVersionUID = 1L
        }
    }
    
    companion object {
        val times = ArrayList<ReminderHolder>()
    }
} 