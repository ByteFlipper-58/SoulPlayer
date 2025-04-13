package com.byteflipper.soulplayer.ui.main // Обновляем пакет

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.byteflipper.soulplayer.data.NavigationType // Обновляем импорт
import com.byteflipper.soulplayer.data.SettingsDataStore // Обновляем импорт
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    settingsDataStore: SettingsDataStore // Используем SettingsDataStore из app модуля
) : ViewModel() {

    // Получаем Flow типа навигации и преобразуем его в StateFlow
    val navigationType: StateFlow<NavigationType> = settingsDataStore.navigationTypeFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000), // Начинаем сбор при наличии подписчиков
            initialValue = NavigationType.BOTTOM // Начальное значение по умолчанию
        )
}
