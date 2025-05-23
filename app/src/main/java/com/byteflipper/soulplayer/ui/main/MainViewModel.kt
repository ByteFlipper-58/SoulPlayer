package com.byteflipper.soulplayer.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.byteflipper.soulplayer.data.NavigationType
import com.byteflipper.soulplayer.data.SettingsDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    settingsDataStore: SettingsDataStore
) : ViewModel() {

    // Получаем Flow типа навигации и преобразуем его в StateFlow
    val navigationType: StateFlow<NavigationType> = settingsDataStore.navigationTypeFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000), // Начинаем сбор при наличии подписчиков
            initialValue = NavigationType.BOTTOM
        )
}
