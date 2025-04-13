package com.byteflipper.soulplayer.presenter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.byteflipper.soulplayer.data.NavigationType
import com.byteflipper.soulplayer.data.SettingsDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsDataStore: SettingsDataStore
) : ViewModel() {

    // Получаем текущий тип навигации как StateFlow
    val navigationType: StateFlow<NavigationType> = settingsDataStore.navigationTypeFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = NavigationType.BOTTOM
        )

    // Функция для изменения типа навигации
    fun setNavigationType(type: NavigationType) {
        viewModelScope.launch {
            settingsDataStore.saveNavigationType(type)
        }
    }
}
