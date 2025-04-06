package com.example.mechanichelper.data.preferences

import android.content.Context
import androidx.core.content.edit

class PreferencesManager(context: Context) {
    private val sharedPreferences = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)

    fun setDarkTheme(enabled: Boolean) {
        sharedPreferences.edit {
            putBoolean("dark_theme", enabled)
        }
    }

    fun isDarkTheme(): Boolean {
        return sharedPreferences.getBoolean("dark_theme", false)
    }

    fun saveSearchHistory(history: List<String>) {
        sharedPreferences.edit {
            putStringSet("search_history", history.toSet())
        }
    }
}