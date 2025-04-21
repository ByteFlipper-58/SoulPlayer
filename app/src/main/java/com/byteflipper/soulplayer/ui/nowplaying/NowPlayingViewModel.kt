package com.byteflipper.soulplayer.ui.nowplaying

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.byteflipper.soulplayer.player.PlayerController
import com.byteflipper.soulplayer.player.PlaybackState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NowPlayingViewModel @Inject constructor(
    private val playerController: PlayerController
) : ViewModel() {

    val playbackState: StateFlow<PlaybackState> = playerController.playbackState
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = PlaybackState()
        )

    fun playPause() {
        viewModelScope.launch {
            if (playbackState.value.isPlaying) {
                 playerController.pause()
             } else {
                 // Возобновляем текущий трек, передавая null, чтобы просто вызвать play()
                 playerController.play(null)
             }
         }
    }

    fun skipToNext() {
        viewModelScope.launch {
            playerController.next()
        }
    }

    fun skipToPrevious() {
        viewModelScope.launch {
            playerController.previous()
        }
    }

    fun seekTo(positionMs: Long) {
        viewModelScope.launch {
            playerController.seekTo(positionMs)
        }
    }
}
