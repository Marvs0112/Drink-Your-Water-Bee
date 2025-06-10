package com.beny.drinkwaterreminder

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.beny.drinkwaterreminder.databinding.ActivityDrinkHistoryBinding
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class DrinkHistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDrinkHistoryBinding
    private lateinit var adapter: MyAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDrinkHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
    }

    private fun setupToolbar() {
        binding.toolbar.apply {
            title = "Drink History"
            setNavigationOnClickListener {
                finish()
            }
            setNavigationIcon(android.R.drawable.ic_menu_close_clear_cancel)
        }
    }

    private fun setupRecyclerView() {
        adapter = MyAdapter(this)
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@DrinkHistoryActivity)
            addItemDecoration(
                MaterialDividerItemDecoration(
                    this@DrinkHistoryActivity,
                    MaterialDividerItemDecoration.VERTICAL
                )
            )
            adapter = this@DrinkHistoryActivity.adapter
        }
    }

    fun refreshList() {
        adapter.notifyDataSetChanged()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    fun saveData(context: Context) {
        val sharedPreferences = context.getSharedPreferences("drinked", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(DrinkedList.Drinked)
        editor.putString("drinked_list", json)
        editor.apply()
    }

    fun transfer_ml(amount: Int) {
        val progressState = getSharedPreferences("progress", Context.MODE_PRIVATE)
        val progress = progressState.getString("progressState", "0")?.toInt() ?: 0
        val newProgress = (progress - amount).coerceAtLeast(0)
        
        progressState.edit().apply {
            putString("progressState", newProgress.toString())
            apply()
        }
    }
} 