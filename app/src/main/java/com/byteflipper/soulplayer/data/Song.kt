package com.byteflipper.soulplayer.data

import android.content.ContentUris
import android.net.Uri

data class Song(
    val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    val duration: Long,
    val data: String, // путь к файлу, может быть неактуально для > API 29
    val albumId: Long,
    val contentUri: Uri?
)

// Свойство-расширение для получения URI обложки альбома
val Song.albumArtUri: Uri
    get() = ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), albumId)
