package com.byteflipper.soulplayer.core

import androidx.media3.exoplayer.ExoPlayer

class ProgressManager(private val player: ExoPlayer) {
    fun getProgress(): Float {
        return if (player.duration > 0) {
            player.currentPosition / player.duration.toFloat()
        } else 0f
    }

    fun seekTo(progress: Float) {
        val position = (progress * player.duration).toLong()
        player.seekTo(position)
    }
}