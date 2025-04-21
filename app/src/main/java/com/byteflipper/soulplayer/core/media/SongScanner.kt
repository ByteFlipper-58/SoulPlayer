package com.byteflipper.soulplayer.core.media

import com.byteflipper.soulplayer.data.Song // Используем существующий Song из app

/**
 * Интерфейс для сканирования песен на устройстве.
 */
interface SongScanner {
    /**
     * Сканирует медиа-хранилище устройства и возвращает список найденных песен.
     * @return Список объектов [Song].
     */
    suspend fun scanSongs(): List<Song>
}
