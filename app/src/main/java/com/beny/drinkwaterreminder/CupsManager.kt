package com.beny.drinkwaterreminder

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.beny.drinkwaterreminder.databinding.ActivityCupsManagerBinding

class CupsManager : AppCompatActivity() {
    private lateinit var binding: ActivityCupsManagerBinding
    private var selectedCupSize: Int = 250
    
    companion object {
        private const val TAG = "CupsManager"
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate called")
        binding = ActivityCupsManagerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        loadSelectedCup()
        setupClickListeners()
    }
    
    private fun loadSelectedCup() {
        selectedCupSize = getSharedPreferences("cupsize", Context.MODE_PRIVATE)
            .getString("cupsize", "250")?.toInt() ?: 250
        Log.d(TAG, "Loaded selected cup size: $selectedCupSize")
        
        updateSelectedCupUI()
    }
    
    private fun setupClickListeners() {
        Log.d(TAG, "Setting up click listeners")
        binding.apply {
            cup100ml.setOnClickListener { selectCup(100) }
            cup150ml.setOnClickListener { selectCup(150) }
            cup200ml.setOnClickListener { selectCup(200) }
            cup250ml.setOnClickListener { selectCup(250) }
            cup300ml.setOnClickListener { selectCup(300) }
            cup400ml.setOnClickListener { selectCup(400) }
            
            cupCustom.setOnClickListener {
                val customSize = customizeEditText.text.toString().toIntOrNull()
                Log.d(TAG, "Custom cup size entered: $customSize")
                if (customSize != null && customSize > 0) {
                    selectCup(customSize)
                } else {
                    Log.w(TAG, "Invalid custom cup size entered")
                    Toast.makeText(this@CupsManager, "Please enter a valid cup size", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    
    private fun selectCup(size: Int) {
        Log.d(TAG, "Selecting cup size: $size")
        selectedCupSize = size
        updateSelectedCupUI()
        saveSelectedCup()
    }
    
    private fun updateSelectedCupUI() {
        Log.d(TAG, "Updating UI for selected cup size: $selectedCupSize")
        binding.apply {
            cup100ml.isSelected = selectedCupSize == 100
            cup150ml.isSelected = selectedCupSize == 150
            cup200ml.isSelected = selectedCupSize == 200
            cup250ml.isSelected = selectedCupSize == 250
            cup300ml.isSelected = selectedCupSize == 300
            cup400ml.isSelected = selectedCupSize == 400
            
            CupSizeText.text = "$selectedCupSize ml"
        }
    }
    
    private fun saveSelectedCup() {
        Log.d(TAG, "Saving selected cup size: $selectedCupSize")
        getSharedPreferences("cupsize", Context.MODE_PRIVATE)
            .edit()
            .putString("cupsize", selectedCupSize.toString())
            .apply()
        
        Toast.makeText(this, "Cup size updated successfully", Toast.LENGTH_SHORT).show()
        finish()
    }
} 