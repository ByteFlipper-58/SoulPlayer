package com.byteflipper.soulplayer.core

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer

class MusicPlayerCore(context: Context) {
    private val player: ExoPlayer = ExoPlayer.Builder(context).build()
    private val playlistManager = PlaylistManager()
    private var isMediaPrepared = false

    fun setMedia(track: MusicTrack) {
        val mediaItem = MediaItem.fromUri(track.data)
        player.setMediaItem(mediaItem)
        player.prepare()
        isMediaPrepared = true
    }

    @JvmName("setPlaylistTracks")
    fun setPlaylist(tracks: List<MusicTrack>) {
        val mediaItems = tracks.map { MediaItem.fromUri(it.data) }
        player.setMediaItems(mediaItems)
        player.prepare()
        isMediaPrepared = true
    }

    @JvmName("setPlaylistUris")
    fun setPlaylist(uris: List<String>) {
        val mediaItems = uris.map { MediaItem.fromUri(it) }
        player.setMediaItems(mediaItems)
        player.prepare()
        isMediaPrepared = true
    }

    fun play() {
        if (isMediaPrepared) {
            player.play()
        } else {
            println("Media is not prepared. Call setMedia or setPlaylist first.")
        }
    }

    fun pause() {
        player.pause()
    }

    fun stop() {
        player.stop()
        player.seekTo(0)
        playlistManager.nextTrack()?.let { setMedia(it) }
    }

    fun release() {
        player.release()
        playlistManager.clearPlaylist()
        isMediaPrepared = false
    }

    fun isPlaying(): Boolean {
        return player.isPlaying
    }
}