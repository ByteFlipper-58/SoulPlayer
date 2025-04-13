package com.byteflipper.soulplayer.di

import android.content.Context
import androidx.media3.exoplayer.ExoPlayer
import com.byteflipper.soulplayer.player.PlayerController
import com.byteflipper.soulplayer.player.media3.ExoPlayerController
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PlayerModule {

    @Binds
    @Singleton
    abstract fun bindPlayerController(
        exoPlayerController: ExoPlayerController
    ): PlayerController

    companion object {
        @Provides
        @Singleton
        fun provideExoPlayer(
            @ApplicationContext context: Context
        ): ExoPlayer {
            return ExoPlayer.Builder(context).build()
        }
    }
}
