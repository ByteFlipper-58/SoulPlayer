package com.byteflipper.soulplayer.player

import com.byteflipper.soulplayer.data.Song

// Состояние воспроизведения
data class PlaybackState(
    val currentSong: Song? = null,
    val isPlaying: Boolean = false,
    val currentPositionMs: Long = 0L,
    val totalDurationMs: Long = 0L
    // TODO: Добавить другие состояния при необходимости (например, repeatMode, shuffleMode)
)
