package com.beny.drinkwaterreminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.beny.drinkwaterreminder.databinding.FragmentHomeBinding
import com.google.gson.Gson
import java.util.*

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: HomeViewModel

    private lateinit var sp: SharedPreferences
    private lateinit var progressState: SharedPreferences
    private var weight: String = "0"
    private var workout: String = "0"
    private var wakeuptime: String = "0"
    private var bedtime: String = "0"
    private var sizeOfCup: String = "250"
    private var goal: String = "0"
    private var progress: String = "0"
    private var personalInformation: Boolean = true
    private var timeDeference: Int = 0
    private var numberOfCups: Int = 1
    private var plusTime: Int = 0

    companion object {
        private const val TAG = "HomeFragment"
        const val RESET_ID = 12
        
        @JvmStatic
        fun newInstance() = HomeFragment()
        
        fun hourExtracter(wakeup: String): Int {
            if (wakeup == "0") return -1
            return try {
                wakeup.split(":")[0].toInt()
            } catch (e: Exception) {
                Log.e(TAG, "Error parsing hour: $wakeup", e)
                -1
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate called")
        context?.let { ctx ->
            NotificationUtils(ctx)
            setResettingProgressBar()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated called")
        
        setupSharedPreferences()
        setupViews()
        setupProgressBar()
        setupClickListeners()
        setupTips()
        startProgressMonitoring()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupSharedPreferences() {
        Log.d(TAG, "Setting up SharedPreferences")
        context?.let { ctx ->
            sp = ctx.getSharedPreferences("first", Context.MODE_PRIVATE)
            progressState = ctx.getSharedPreferences("progress", Context.MODE_PRIVATE)
            
            weight = sp.getString("weight", "0") ?: "0"
            workout = sp.getString("workout", "0") ?: "0"
            wakeuptime = sp.getString("wakeupTime", "0") ?: "0"
            bedtime = sp.getString("bedtime", "0") ?: "0"
            
            Log.d(TAG, "Loaded preferences - Weight: $weight, Workout: $workout, " +
                      "Wakeup: $wakeuptime, Bedtime: $bedtime")
            
            progress = progressState.getString("progressState", "0") ?: "0"
            
            // First try to load goal from dailyGoal preferences
            goal = ctx.getSharedPreferences("dailyGoal", Context.MODE_PRIVATE)
                .getInt("goal", 0).toString()
            
            // If no goal is set in dailyGoal, try the old location
            if (goal == "0") {
                goal = progressState.getString("goalState", "0") ?: "0"
            }
            
            sizeOfCup = ctx.getSharedPreferences("cupsize", Context.MODE_PRIVATE)
                .getString("cupsize", "250") ?: "250"
            
            Log.d(TAG, "Loaded progress - Current: $progress, Goal: $goal, Cup Size: $sizeOfCup")
            
            personalInformation = weight != "0" && workout != "0" && 
                                wakeuptime != "0" && bedtime != "0"
            
            progressState.edit()
                .putBoolean("personal_Information", personalInformation)
                .apply()
        }
    }

    override fun onResume() {
        super.onResume()
        context?.let { ctx ->
            sizeOfCup = ctx.getSharedPreferences("cupsize", Context.MODE_PRIVATE)
                .getString("cupsize", "250") ?: "250"
            Log.d(TAG, "Reloaded cup size: $sizeOfCup")
            
            setupSharedPreferences()
            setupProgressBar()
            setupViews()
        }
    }

    private fun setupViews() {
        binding.apply {
            progressBar.max = goal.toInt()
            progressBar.progress = progress.toInt()
            updateProgressText()
        }
    }

    private fun setupProgressBar() {
        val wakeHour = hourExtracter(wakeuptime)
        val bedHour = hourExtracter(bedtime)
        Log.d(TAG, "Setting up progress bar - Wake Hour: $wakeHour, Bed Hour: $bedHour")

        if (wakeHour != -1 && goal == "0") {
            val pi = PersonalInformation(
                workout.toInt(),
                wakeuptime,
                bedtime,
                weight.toDouble()
            )
            goal = pi.GoalCalculator().toInt().toString()
            Log.d(TAG, "Calculated new goal: $goal")
            
            // Save the calculated goal
            context?.getSharedPreferences("dailyGoal", Context.MODE_PRIVATE)?.edit()?.apply {
                putInt("goal", goal.toInt())
                apply()
            }
        }

        timeDeference = bedHour - wakeHour
        numberOfCups = goal.toInt() / sizeOfCup.toInt().coerceAtLeast(1)
        plusTime = timeDeference / numberOfCups.coerceAtLeast(1)
        
        Log.d(TAG, "Progress bar setup complete - Time Difference: $timeDeference, " +
                  "Number of Cups: $numberOfCups, Plus Time: $plusTime")
    }

    private fun setupClickListeners() {
        binding.apply {
            drinkWaterBtn.setOnClickListener {
                Log.d(TAG, "Drink water button clicked")
                if (!personalInformation) {
                    Log.d(TAG, "Personal information not set, opening Pi activity")
                    startActivity(Intent(context, Pi::class.java))
                    return@setOnClickListener
                }
                
                // Stop any currently playing alarm
                WaterReminderReceiver.stopCurrentAlarm()
                
                progressBar.max = goal.toInt()
                progressBar.incrementProgressBy(sizeOfCup.toInt())
                
                goal = progressBar.max.toString()
                progress = progressBar.progress.toString()
                updateProgressText()
                
                Log.d(TAG, "Updated progress - Current: $progress, Goal: $goal")
                
                if (progress.toInt() >= goal.toInt()) {
                    Log.d(TAG, "Goal reached!")
                    Toast.makeText(context, "Congratulations! You reached your goal", Toast.LENGTH_LONG).show()
                    
                    android.app.AlertDialog.Builder(requireContext())
                        .setTitle("Goal Achieved!")
                        .setMessage("Would you like to reset your progress for a new goal?")
                        .setPositiveButton("Yes") { _, _ ->
                            resetProgress()
                        }
                        .setNegativeButton("No", null)
                        .show()
                }

                val currentTime = Calendar.getInstance().time
                val drinkObj = DrinkedList(currentTime.toString(), sizeOfCup.toInt())
                DrinkedList.Drinked.add(drinkObj)
                
                saveData()
            }

            List.setOnClickListener {
                Log.d(TAG, "Opening drink history")
                startActivity(Intent(context, DrinkHistoryActivity::class.java))
            }

            changeCup.setOnClickListener {
                Log.d(TAG, "Opening cup manager")
                startActivity(Intent(context, CupsManager::class.java))
            }
        }
    }

    private fun setupTips() {
        binding.apply {
            val randomTip = Tips.tips.random()
            tipstxt.text = randomTip
            
            tipstxt.setOnClickListener {
                startActivity(Intent(context, Tips::class.java))
            }
        }
    }

    private fun startProgressMonitoring() {
        Handler(Looper.getMainLooper()).apply {
            val delay = 5000L
            
            val runnable = object : Runnable {
                override fun run() {
                    context?.let { ctx ->
                        val deletedCup = ctx.getSharedPreferences("deleteCup", Context.MODE_PRIVATE)
                            .getInt("deleteCup", 0)
                        
                        if (deletedCup > 0) {
                            binding.progressBar.incrementProgressBy(-deletedCup)
                            ctx.getSharedPreferences("deleteCup", Context.MODE_PRIVATE)
                                .edit()
                                .putInt("deleteCup", 0)
                                .apply()
                            
                            progress = binding.progressBar.progress.toString()
                            updateProgressText()
                            saveData()
                        }
                        
                        postDelayed(this, delay)
                    }
                }
            }
            
            postDelayed(runnable, delay)
        }
    }

    private fun setResettingProgressBar() {
        context?.let { ctx ->
            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
            }

            val intent = Intent(ctx, Reset_ProgressBar::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                ctx,
                RESET_ID,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val alarmManager = ctx.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
            alarmManager?.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                AlarmManager.INTERVAL_DAY,
                pendingIntent
            )
        }
    }

    private fun saveData() {
        context?.let { ctx ->
            // Save progress in progress preferences
            ctx.getSharedPreferences("progress", Context.MODE_PRIVATE)
                .edit()
                .putString("progressState", binding.progressBar.progress.toString())
                .apply()
                
            // Save goal in dailyGoal preferences
            ctx.getSharedPreferences("dailyGoal", Context.MODE_PRIVATE)
                .edit()
                .putInt("goal", binding.progressBar.max)
                .apply()
        }
    }

    private fun updateProgressText() {
        binding.apply {
            goaltxt.text = "${goal}ml"
            Progresstxt.text = "${progress}ml"
        }
    }

    private fun resetProgress() {
        Log.d(TAG, "Resetting progress")
        context?.let { ctx ->
            // Clear the drink history
            DrinkedList.Drinked.clear()
            
            // Reset progress
            progress = "0"
            binding.progressBar.progress = 0
            
            // Reset in SharedPreferences
            ctx.getSharedPreferences("progress", Context.MODE_PRIVATE)
                .edit()
                .putString("progressState", "0")
                .apply()
                
            // Reset daily goal in SharedPreferences
            ctx.getSharedPreferences("dailyGoal", Context.MODE_PRIVATE)
                .edit()
                .putInt("goal", goal.toInt())
                .apply()
            
            // Update UI
            updateProgressText()
            
            // Show confirmation
            Toast.makeText(ctx, "Progress has been reset for a new day!", Toast.LENGTH_SHORT).show()
            
            // Notify adapter if in DrinkHistoryActivity
            (activity as? DrinkHistoryActivity)?.refreshList()
        }
    }

    private fun onDrinkButtonClicked() {
        // Stop any currently playing alarm
        WaterReminderReceiver.stopCurrentAlarm()
        
        // Update water intake
        viewModel.addWaterIntake()
        
        // Show success message
        Toast.makeText(requireContext(), "Great job staying hydrated!", Toast.LENGTH_SHORT).show()
    }
} 