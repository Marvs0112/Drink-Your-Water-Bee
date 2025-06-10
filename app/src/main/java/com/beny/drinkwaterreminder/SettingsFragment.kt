package com.beny.drinkwaterreminder

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.beny.drinkwaterreminder.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    
    private val settingList = listOf(
        "Notification Manager",
        "Cups Manager",
        "Personal information",
        "Daily goal",
        "Contact us",
        "Share"
    )
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListView()
    }
    
    override fun onResume() {
        super.onResume()
        // Refresh the list adapter to ensure click listeners are active
        setupListView()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    
    private fun setupListView() {
        val arrayAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            settingList
        )
        
        binding.ListView.apply {
            adapter = arrayAdapter
            setOnItemClickListener { _, _, position, _ ->
                when (position) {
                    0 -> startActivity(Intent(context, NotificationReminder::class.java))
                    1 -> startActivity(Intent(context, CupsManager::class.java))
                    2 -> startActivity(Intent(context, Pi::class.java))
                    3 -> showDailyGoalDialog()
                    4 -> handleContactUs()
                    5 -> handleShare()
                }
            }
        }
    }
    
    private fun showDailyGoalDialog() {
        val dialog = DailyGoalDialog()
        dialog.show(childFragmentManager, "DailyGoalDialog")
    }
    
    private fun handleContactUs() {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "message/rfc822"
            putExtra(Intent.EXTRA_EMAIL, arrayOf("support@drinkwaterreminder.com"))
            putExtra(Intent.EXTRA_SUBJECT, "Drink Water Reminder Support")
        }
        startActivity(Intent.createChooser(intent, "Send Email"))
    }
    
    private fun handleShare() {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Drink Water Reminder")
            putExtra(Intent.EXTRA_TEXT, "Stay hydrated with Drink Water Reminder!")
        }
        startActivity(Intent.createChooser(shareIntent, "Share via"))
    }
    
    companion object {
        @JvmStatic
        fun newInstance() = SettingsFragment()
    }
} 