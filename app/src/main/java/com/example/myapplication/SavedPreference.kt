package com.johnnyelachi

import android.content.Context

class SavedPreference {

    companion object {
        private const val PREF_EMAIL = "pref_email"
        private const val PREF_USERNAME = "pref_username"

        fun setEmail(context: Context, email: String) {
            val preferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
            preferences.edit().putString(PREF_EMAIL, email).apply()
        }

        fun getEmail(context: Context): String {
            val preferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
            return preferences.getString(PREF_EMAIL, "") ?: ""
        }

        fun setUsername(context: Context, username: String) {
            val preferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
            preferences.edit().putString(PREF_USERNAME, username).apply()
        }

        fun getUsername(context: Context): String {
            val preferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
            return preferences.getString(PREF_USERNAME, "") ?: ""
        }
    }
}
