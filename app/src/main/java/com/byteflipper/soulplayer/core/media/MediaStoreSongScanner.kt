package com.byteflipper.soulplayer.core.media

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.byteflipper.soulplayer.data.Song
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Реализация [SongScanner], использующая [ContentResolver] для сканирования MediaStore.
 */
class MediaStoreSongScanner @Inject constructor(
    private val context: Context
) : SongScanner {

    override suspend fun scanSongs(): List<Song> = withContext(Dispatchers.IO) {
        val songs = mutableListOf<Song>()
        val collection =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
            } else {
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            }

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATA, // Путь к файлу (может быть недоступен в Android 10+)
            MediaStore.Audio.Media.ALBUM_ID
        )

        // Добавляем условие, чтобы выбирать только музыкальные файлы
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"

        val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"

        context.contentResolver.query(
            collection,
            projection,
            selection,
            null, // selectionArgs не нужны, так как нет плейсхолдеров в selection
            sortOrder
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
            val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
            val albumIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val title = cursor.getString(titleColumn)
                val artist = cursor.getString(artistColumn)
                val album = cursor.getString(albumColumn)
                val duration = cursor.getLong(durationColumn)
                val data = cursor.getString(dataColumn)
                val albumId = cursor.getLong(albumIdColumn)
                val contentUri: Uri = ContentUris.withAppendedId(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    id
                )

                songs.add(
                    Song(
                        id = id,
                        title = title ?: "Unknown Title",
                        artist = artist ?: "Unknown Artist",
                        album = album ?: "Unknown Album",
                        duration = duration,
                        data = data ?: "",
                        albumId = albumId,
                        contentUri = contentUri
                    )
                )
            }
        }
        songs
    }
}
