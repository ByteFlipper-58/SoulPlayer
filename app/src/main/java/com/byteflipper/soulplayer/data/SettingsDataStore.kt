package com.byteflipper.soulplayer.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

enum class NavigationType {
    BOTTOM, TABS
}

@Singleton
class SettingsDataStore @Inject constructor(@ApplicationContext private val context: Context) {

    private val navigationTypeKey = stringPreferencesKey("navigation_type")

    val navigationTypeFlow: Flow<NavigationType> = context.dataStore.data
        .map { preferences ->
            val typeString = preferences[navigationTypeKey] ?: NavigationType.BOTTOM.name
            try {
                NavigationType.valueOf(typeString)
            } catch (e: IllegalArgumentException) {
                NavigationType.BOTTOM
            }
        }

    suspend fun saveNavigationType(navigationType: NavigationType) {
        context.dataStore.edit { settings ->
            settings[navigationTypeKey] = navigationType.name
        }
    }
}
