package com.byteflipper.soulplayer.ui.songs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import com.byteflipper.soulplayer.core.media.SongScanner // Импорт из нового места
import com.byteflipper.soulplayer.data.Song // Используем Song из app.data
import com.byteflipper.soulplayer.player.PlayerController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
// Убираем псевдоним, так как используем Song из app.data напрямую

@HiltViewModel
class SongsViewModel @Inject constructor(
    private val songScanner: SongScanner,
    private val playerController: PlayerController // Внедряем PlayerController
) : ViewModel() {

    private val _songs = MutableStateFlow<List<Song>>(emptyList()) // Используем Song напрямую
    val songs: StateFlow<List<Song>> = _songs.asStateFlow() // Используем Song напрямую

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadSongs()
    }

    fun loadSongs() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _songs.value = songScanner.scanSongs()
            } catch (e: Exception) {
                // Обработка ошибок (например, логирование или показ сообщения пользователю)
                _songs.value = emptyList() // Показать пустой список в случае ошибки
                println("Error loading songs: ${e.message}")
            } finally {
                _isLoading.value = false
            } // Добавляем недостающую скобку finally
        } // Эта скобка закрывает launch
    }

    /**
     * Начинает воспроизведение выбранной песни из текущего списка.
     * @param index Индекс песни в списке `_songs.value`.
     */
    fun playSong(index: Int) {
        val currentSongs = _songs.value
        if (index < 0 || index >= currentSongs.size) {
            println("Error: Invalid song index $index")
            return
        }

        // Используем методы из PlayerController
        viewModelScope.launch { // Вызовы PlayerController должны быть в корутине
            try {
                playerController.setPlaylist(currentSongs, false) // Устанавливаем плейлист
                playerController.play(currentSongs[index]) // Начинаем играть с выбранной песни
            } catch (e: Exception) {
                println("Error playing song: ${e.message}")
                // TODO: Обработать ошибку (например, показать Snackbar)
            }
        }
    }
}
