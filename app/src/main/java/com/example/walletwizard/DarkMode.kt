package com.example.walletwizard

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first


class DarkMode private constructor(context: Context) {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("settings")
    private val dataStore = context.dataStore

    suspend fun saveDarkModeEnabled(flag:Boolean){
        val dataStoreKey = booleanPreferencesKey("dark_mode")

        dataStore.edit { settings->
            settings[dataStoreKey] = flag
        }
    }

    suspend fun isDarkModeEnabled():Boolean {
        val dataStoreKey = booleanPreferencesKey("dark_mode")

        val preferences = dataStore.data.first()
        return preferences[dataStoreKey] ?: false
    }

    companion object{
        private var instance:DarkMode? = null

        fun getInstance(context: Context):DarkMode{
            instance = if(instance == null) DarkMode(context) else instance

            return instance!!
        }
    }
}