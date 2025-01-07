// DataStoreManager.kt
package com.byteflipper.soulplayer.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")


class DataStoreManager(private val context: Context) {

    private object PreferencesKeys {
        val THEME_KEY = stringPreferencesKey("app_theme")
        val DYNAMIC_COLOR_KEY = booleanPreferencesKey("dynamic_color")
    }


    suspend fun <T> save(key: Preferences.Key<T>, value: T) {
        context.dataStore.edit { settings ->
            settings[key] = value
        }
    }

    fun <T> read(key: Preferences.Key<T>, defaultValue: T): Flow<T> = context.dataStore.data
        .map { settings ->
            settings[key] ?: defaultValue
        }

    suspend fun setTheme(theme: String) {
        save(PreferencesKeys.THEME_KEY, theme)
    }

    fun getTheme() = read(PreferencesKeys.THEME_KEY, "system") // "system", "light", "dark"

    suspend fun setDynamicColor(dynamicColor: Boolean) {
        save(PreferencesKeys.DYNAMIC_COLOR_KEY, dynamicColor)
    }

    fun getDynamicColor() = read(PreferencesKeys.DYNAMIC_COLOR_KEY, true)
}