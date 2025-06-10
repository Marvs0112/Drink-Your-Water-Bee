package com.beny.drinkwaterreminder

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.beny.drinkwaterreminder.databinding.DialogDailyGoalBinding

class DailyGoalDialog : DialogFragment() {
    private var _binding: DialogDailyGoalBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogDailyGoalBinding.inflate(layoutInflater)
        
        // Load current goal
        val currentGoal = context?.getSharedPreferences("dailyGoal", Context.MODE_PRIVATE)
            ?.getInt("goal", 0) ?: 0
        binding.goalInput.setText(if (currentGoal > 0) currentGoal.toString() else "")
        
        return AlertDialog.Builder(requireContext())
            .setTitle("Set Daily Goal")
            .setView(binding.root)
            .setPositiveButton("Save") { _, _ ->
                handleSave()
            }
            .setNegativeButton("Cancel", null)
            .create()
    }

    private fun handleSave() {
        val goalInput = binding.goalInput.text.toString()
        if (goalInput.isNotEmpty()) {
            try {
                val goal = goalInput.toInt()
                if (goal > 0) {
                    saveGoal(goal)
                    Toast.makeText(context, "Daily goal updated successfully", Toast.LENGTH_SHORT).show()
                    // Refresh the home fragment
                    (activity as? MainActivity)?.let { activity ->
                        activity.supportFragmentManager.fragments.forEach { fragment ->
                            if (fragment is HomeFragment) {
                                fragment.onResume()
                            }
                        }
                    }
                } else {
                    Toast.makeText(context, "Please enter a valid goal greater than 0", Toast.LENGTH_SHORT).show()
                }
            } catch (e: NumberFormatException) {
                Toast.makeText(context, "Please enter a valid number", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Please enter a goal", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveGoal(goal: Int) {
        context?.let { ctx ->
            // Save in dailyGoal preferences
            ctx.getSharedPreferences("dailyGoal", Context.MODE_PRIVATE)?.edit()?.apply {
                putInt("goal", goal)
                apply()
            }
            
            // Also save in progress preferences for backward compatibility
            ctx.getSharedPreferences("progress", Context.MODE_PRIVATE)?.edit()?.apply {
                putString("goalState", goal.toString())
                apply()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 