package com.byteflipper.soulplayer.core

class ProgressManager(private val musicPlayerCore: MusicPlayerCore) {

    fun getProgress(): Float {
        val duration = musicPlayerCore.getDuration()
        val position = musicPlayerCore.getCurrentPosition()
        return if (duration > 0) position.toFloat() / duration.toFloat() else 0f
    }

    fun seekTo(progress: Float) {
        val duration = musicPlayerCore.getDuration()
        val newPosition = (progress * duration).toLong()
        musicPlayerCore.seekTo(newPosition)
    }
}