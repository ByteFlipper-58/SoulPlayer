package com.byteflipper.soulplayer.core.di

import android.content.Context
import com.byteflipper.soulplayer.core.media.MediaStoreSongScanner
import com.byteflipper.soulplayer.core.media.SongScanner
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MediaModule {

    @Provides
    @Singleton
    fun provideSongScanner(@ApplicationContext context: Context): SongScanner {
        return MediaStoreSongScanner(context)
    }
}
