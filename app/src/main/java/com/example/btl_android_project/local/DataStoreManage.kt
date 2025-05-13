package com.example.btl_android_project.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.btl_android_project.notification.CaloriesReminderHelper
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "static_data_prefs")

class DataStoreManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val caloriesReminderHelper: CaloriesReminderHelper
) {

    companion object {
        val IS_STATIC_DATA_LOADED = booleanPreferencesKey("is_static_data_loaded")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
    }

    suspend fun setStaticDataLoaded(isLoaded: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[IS_STATIC_DATA_LOADED] = isLoaded
        }
    }

    val isStaticDataLoaded: Flow<Boolean> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[IS_STATIC_DATA_LOADED] == true
        }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[NOTIFICATIONS_ENABLED] = enabled
        }
        if (enabled) {
            caloriesReminderHelper.setupReminderNotifications(context)
        } else {
            caloriesReminderHelper.cancelAllReminders(context)
        }
    }

    val isNotificationsEnabled: Flow<Boolean> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[NOTIFICATIONS_ENABLED] == true
        }
}