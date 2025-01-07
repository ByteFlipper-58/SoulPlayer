package com.byteflipper.soulplayer.core

class PlaylistManager {
    private val playlist = mutableListOf<MusicTrack>()
    var currentIndex = 0
        private set

    fun addTrack(track: MusicTrack) {
        playlist.add(track)
    }

    fun addTracks(tracks: List<MusicTrack>) {
        playlist.addAll(tracks)
    }

    fun nextTrack(): MusicTrack? {
        if(playlist.isNotEmpty() && currentIndex < playlist.size-1)
        {
            currentIndex++
        }
        return playlist.getOrNull(currentIndex)
    }

    fun previousTrack(): MusicTrack? {
        if (playlist.isNotEmpty() && currentIndex > 0) {
            currentIndex--
        }
        return playlist.getOrNull(currentIndex)
    }

    fun getCurrentTrack(): MusicTrack? {
        return playlist.getOrNull(currentIndex)
    }

    fun clearPlaylist() {
        playlist.clear()
        currentIndex = 0
    }
}