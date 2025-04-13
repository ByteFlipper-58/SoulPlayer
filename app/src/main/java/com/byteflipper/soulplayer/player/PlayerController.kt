package com.byteflipper.soulplayer.player

import com.byteflipper.soulplayer.data.Song
import kotlinx.coroutines.flow.StateFlow

interface PlayerController {
    val playbackState: StateFlow<PlaybackState>

    suspend fun play(song: Song? = null)
    suspend fun pause()
    suspend fun next()
    suspend fun previous()
    suspend fun seekTo(positionMs: Long)
    suspend fun setPlaylist(songs: List<Song>, startPlaying: Boolean = false)
    fun release()
}
