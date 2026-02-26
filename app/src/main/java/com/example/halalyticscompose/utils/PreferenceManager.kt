package com.example.halalyticscompose.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class PreferenceManager(private val context: Context) {

    companion object {
        val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")
        val ALLERGY_KEY = stringPreferencesKey("user_allergies") // Comma separated string
        val LANGUAGE_KEY = stringPreferencesKey("app_language")
        val NOTIF_ENABLED_KEY = booleanPreferencesKey("notif_enabled")
        val WATCHLIST_KEY = stringPreferencesKey("user_watchlist") // Comma separated string
    }

    val isDarkMode: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[DARK_MODE_KEY] ?: false
    }

    val userAllergies: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[ALLERGY_KEY] ?: ""
    }

    val appLanguage: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[LANGUAGE_KEY] ?: "id"
    }

    val isNotifEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[NOTIF_ENABLED_KEY] ?: true
    }

    val userWatchlist: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[WATCHLIST_KEY] ?: ""
    }

    suspend fun setDarkMode(isEnabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[DARK_MODE_KEY] = isEnabled
        }
    }

    suspend fun setAllergies(allergies: String) {
        context.dataStore.edit { preferences ->
            preferences[ALLERGY_KEY] = allergies
        }
    }

    suspend fun setLanguage(languageCode: String) {
        context.dataStore.edit { preferences ->
            preferences[LANGUAGE_KEY] = languageCode
        }
    }

    suspend fun setNotifEnabled(isEnabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[NOTIF_ENABLED_KEY] = isEnabled
        }
    }

    suspend fun setWatchlist(watchlist: String) {
        context.dataStore.edit { preferences ->
            preferences[WATCHLIST_KEY] = watchlist
        }
    }
}

object AllergyConstants {
    val ALLERGY_LIST = listOf(
        "Kacang",
        "Telur",
        "Susu",
        "Ikan",
        "Kerang",
        "Gandum (Gluten)",
        "Kedelai",
        "Udang",
        "Kepiting"
    )
}
