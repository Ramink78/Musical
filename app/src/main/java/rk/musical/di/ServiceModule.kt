package rk.musical.di

import android.content.Context
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.exoplayer.ExoPlayer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import rk.musical.data.AlbumRepository
import rk.musical.data.MediaTree
import rk.musical.data.SongRepository

@InstallIn(ServiceComponent::class)
@Module
class ServiceModule {

    @Provides
    fun provideAudioAttributes(): AudioAttributes {
        return AudioAttributes.Builder()
            .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
            .setSpatializationBehavior(C.SPATIALIZATION_BEHAVIOR_AUTO)
            .setUsage(C.USAGE_MEDIA)
            .build()
    }

    @Provides
    fun provideExoPlayer(
        @ApplicationContext context: Context,
        audioAttributes: AudioAttributes
    ): ExoPlayer {
        return ExoPlayer.Builder(context)
            .setHandleAudioBecomingNoisy(true)
            .setAudioAttributes(audioAttributes, true)
            .build()
    }

    @Provides
    fun provideSongRepository(@ApplicationContext context: Context) =
        SongRepository(context = context)


    @Provides
    fun provideAlbumRepository(@ApplicationContext context: Context) =
        AlbumRepository(context = context)
    
}