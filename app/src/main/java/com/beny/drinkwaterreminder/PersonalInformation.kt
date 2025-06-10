package com.beny.drinkwaterreminder

class PersonalInformation(
    private val workout: Int,
    private val wakeupTime: String,
    private val bedTime: String,
    private val weight: Double
) {
    fun GoalCalculator(): Double {
        // Base calculation: weight * 0.033 gives liters, * 1000 converts to ml
        val baseAmount = weight * 0.033 * 1000
        
        // Add extra water based on workout intensity
        val workoutExtra = when (workout) {
            0 -> 0.0     // No workout
            1 -> 350.0   // Light workout
            2 -> 500.0   // Moderate workout
            3 -> 1000.0  // Intense workout
            else -> 0.0
        }
        
        return baseAmount + workoutExtra
    }
    
    fun getWorkout(): Int = workout
    
    fun getWakeupTime(): String = wakeupTime
    
    fun getBedTime(): String = bedTime
    
    fun getWeight(): Double = weight
} 