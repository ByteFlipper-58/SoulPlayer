// MusicScanner.kt
package com.byteflipper.soulplayer.core

import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.lang.IllegalArgumentException

data class MusicTrack(
    val data: String,
    val title: String,
    val artist: String,
    val album: String,
    val duration: Long,
    val id: Long,
    val cover: Bitmap? = null
)

class MusicScanner(private val context: Context) {

    private val SUPPORTED_MIME_TYPES = arrayOf(
        "audio/mpeg", // MP3
        "audio/aac", // AAC
        "audio/x-flac", // FLAC
        "audio/ogg",   // OGG
        "audio/opus", // Opus
        "audio/x-wav", // WAV
    )

    suspend fun scanMusic(): List<MusicTrack> = withContext(Dispatchers.IO) {
        val musicList = mutableListOf<MusicTrack>()
        val uri: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

        val projection = arrayOf(
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media._ID
        )

        val selection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            "${MediaStore.Audio.Media.IS_MUSIC} != 0 AND ${MediaStore.Audio.Media.MIME_TYPE} IN (${SUPPORTED_MIME_TYPES.joinToString { "?" }})"
        } else {
            "${MediaStore.Audio.Media.IS_MUSIC} != 0 AND ${MediaStore.Audio.Media.MIME_TYPE} IN (${SUPPORTED_MIME_TYPES.joinToString { "?" }})"
        }

        val cursor: Cursor? = context.contentResolver.query(
            uri,
            projection,
            selection,
            SUPPORTED_MIME_TYPES,
            null
        )

        cursor?.use {
            try {
                val dataIndex = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
                val titleIndex = it.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                val artistIndex = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                val albumIndex = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
                val durationIndex = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
                val idIndex = it.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                while (it.moveToNext()) {
                    val track = MusicTrack(
                        data = it.getString(dataIndex),
                        title = it.getString(titleIndex) ?: "Unknown Title",
                        artist = it.getString(artistIndex) ?: "Unknown Artist",
                        album = it.getString(albumIndex) ?: "Unknown Album",
                        duration = it.getLong(durationIndex),
                        id = it.getLong(idIndex),
                        cover = loadCover(it.getString(dataIndex))
                    )
                    musicList.add(track)
                }
            } catch (e: IllegalArgumentException){
                Log.e("MusicScanner", "Error reading column", e)
            }
        }
        return@withContext musicList
    }

    private fun loadCover(path: String): Bitmap? {
        try {
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(path)
            val coverBytes = retriever.embeddedPicture
            retriever.release()
            return if (coverBytes != null){
                val inputStream = ByteArrayInputStream(coverBytes)
                BitmapFactory.decodeStream(inputStream)
            } else null

        } catch (e: Exception){
            Log.e("MusicScanner", "Error loading cover", e)
            return null
        }
    }
}