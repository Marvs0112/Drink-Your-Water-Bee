package com.beny.drinkwaterreminder

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.beny.drinkwaterreminder.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayoutMediator
import androidx.core.app.NotificationManagerCompat
import android.app.NotificationChannel
import android.app.NotificationManager
import androidx.fragment.app.Fragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    
    companion object {
        private const val TAG = "MainActivity"
        const val PREF_TAG = "delCup"
        const val CHANNEL_ID = "water_reminder_channel"
        const val CHANNEL_NAME = "reminder"
        lateinit var context: Context
            private set
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        context = applicationContext
        checkFirstRun()
        NotificationUtils(this)
        handleIntent()
        
        createNotificationChannel()
        
        // Check if launched from notification
        if (intent?.getBooleanExtra("FROM_NOTIFICATION", false) == true) {
            // The activity was launched from the notification
            val reminderId = intent.getIntExtra("REMINDER_ID", -1)
            if (reminderId != -1) {
                // Clear the notification
                NotificationManagerCompat.from(this).cancel(reminderId)
            }
        }
        
        // Set up bottom navigation
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    loadFragment(HomeFragment())
                    true
                }
                R.id.navigation_settings -> {
                    loadFragment(SettingsFragment())
                    true
                }
                else -> false
            }
        }
        
        // Load default fragment
        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
        }
    }

    private fun handleIntent() {
        intent?.let { intent ->
            val ml = intent.getIntExtra("ml", -1)
            getSharedPreferences(PREF_TAG, Context.MODE_PRIVATE).edit().apply {
                putInt("ml", ml)
                putBoolean("isDelete", true)
                apply()
            }

            val clicked = intent.getBooleanExtra("CLicked", false)
            if (clicked) {
                DailyGoalDialog().show(supportFragmentManager, "Daily goal")
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.notif_menu -> {
                Toast.makeText(this, "Notification is On!", Toast.LENGTH_SHORT).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun checkFirstRun() {
        val PREFS_NAME = "MyPrefsFile"
        val DOESNT_EXIST = -1

        val currentVersionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            packageManager.getPackageInfo(packageName, 0).longVersionCode.toInt()
        } else {
            @Suppress("DEPRECATION")
            packageManager.getPackageInfo(packageName, 0).versionCode
        }
        
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val savedVersionCode = prefs.getInt("version_code", DOESNT_EXIST)

        when {
            currentVersionCode == savedVersionCode -> {
                getSharedPreferences("isFirstTime", Context.MODE_PRIVATE).edit().apply {
                    putBoolean("isFirstTime", false)
                    apply()
                }
            }
            savedVersionCode == DOESNT_EXIST -> {
                startActivity(Intent(this, Pi::class.java))
                getSharedPreferences("isFirstTime", Context.MODE_PRIVATE).edit().apply {
                    putBoolean("isFirstTime", true)
                    apply()
                }
            }
            currentVersionCode > savedVersionCode -> {
                // Handle upgrade scenario
            }
        }

        prefs.edit().putInt("version_code", currentVersionCode).apply()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
} 