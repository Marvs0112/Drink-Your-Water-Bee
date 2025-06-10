package com.beny.drinkwaterreminder

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.beny.drinkwaterreminder.databinding.ActivityListBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class List : AppCompatActivity() {
    private lateinit var binding: ActivityListBinding
    private lateinit var myAdapter: MyAdapter
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        loadData()
        setupRecyclerView()
    }
    
    private fun setupRecyclerView() {
        myAdapter = MyAdapter(this)
        binding.recycleView.apply {
            adapter = myAdapter
            layoutManager = LinearLayoutManager(this@List)
        }
        
        val itemTouchHelper = ItemTouchHelper(swipeCallback)
        itemTouchHelper.attachToRecyclerView(binding.recycleView)
    }
    
    private val swipeCallback = object : ItemTouchHelper.SimpleCallback(
        0, 
        ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT
    ) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean = false
        
        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val position = viewHolder.absoluteAdapterPosition
            val amount = DrinkedList.Drinked[position].amount
            
            DrinkedList.Drinked.removeAt(position)
            myAdapter.notifyItemRemoved(position)
            saveData(this@List)
            transfer_ml(amount)
        }
    }
    
    fun saveData(context: Context) {
        context.getSharedPreferences("saveArrayList", Context.MODE_PRIVATE)
            .edit()
            .putString("drinkObj", Gson().toJson(DrinkedList.Drinked))
            .apply()
    }
    
    private fun loadData() {
        val sharedPreferences = getSharedPreferences("saveArrayList", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString("drinkObj", null)
        
        if (json != null) {
            val type = object : TypeToken<ArrayList<DrinkedList>>() {}.type
            DrinkedList.Drinked.clear()
            DrinkedList.Drinked.addAll(gson.fromJson(json, type))
        }
    }
    
    fun transfer_ml(amount: Int) {
        getSharedPreferences("deleteCup", Context.MODE_PRIVATE)
            .edit()
            .putInt("deleteCup", amount)
            .apply()
    }
} 