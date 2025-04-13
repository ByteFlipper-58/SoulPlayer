package com.byteflipper.soulplayer.data

import android.net.Uri

// TODO: Добавить необходимые поля (например, album, duration, artworkUri и т.д.)
data class Song(
    val id: String,
    val title: String,
    val artist: String,
    val uri: Uri
)
