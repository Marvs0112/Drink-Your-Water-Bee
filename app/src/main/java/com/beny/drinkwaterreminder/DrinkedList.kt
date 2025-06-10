package com.beny.drinkwaterreminder

import java.io.Serializable

data class DrinkedList(
    val time: String,
    val amount: Int
) : Serializable {
    
    override fun toString(): String = "$time: $amount ml"
    
    companion object {
        private const val serialVersionUID = 1L
        val Drinked = ArrayList<DrinkedList>()
    }
} 