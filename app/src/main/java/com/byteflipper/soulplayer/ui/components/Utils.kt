package com.byteflipper.soulplayer.ui.components

fun formatDuration(durationMicros: Long): String {
    val totalSeconds = durationMicros / 1_000_000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d", minutes, seconds)
}