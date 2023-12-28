package com.example.myapplication

import android.content.Context
import android.content.SharedPreferences

class SharedPrefsManager(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("coinPrefs", Context.MODE_PRIVATE)

    fun savePurchasedMinutes(minutes: Int) {
        val editor = sharedPreferences.edit()
        editor.putInt("userPurchasedMinutes", minutes)
        editor.apply()
    }

    fun getPurchasedMinutes(): Int {
        return sharedPreferences.getInt("userPurchasedMinutes", 0)
    }
}