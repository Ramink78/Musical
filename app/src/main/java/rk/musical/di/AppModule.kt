package rk.musical.di

import android.content.Context
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.exoplayer.ExoPlayer
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import rk.musical.data.AlbumRepository
import rk.musical.data.FavoriteRepository
import rk.musical.data.LyricRepository
import rk.musical.data.MediaItemTreeImpl
import rk.musical.data.SongRepository
import rk.musical.data.db.LyricDao
import rk.musical.data.db.MusicalDatabase
import rk.playbackservice.MediaItemTree

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideMusicalDatabase(@ApplicationContext context: Context) =
        Room.databaseBuilder(
            context = context,
            klass = MusicalDatabase::class.java,
            name = "MusicalDatabase"
        )
            .build()

    @Provides
    fun provideLyricDao(db: MusicalDatabase) =
        db.lyricDao()

    @Singleton
    @Provides
    fun provideAudioAttributes(): AudioAttributes {
        return AudioAttributes.Builder()
            .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
            .setSpatializationBehavior(C.SPATIALIZATION_BEHAVIOR_AUTO)
            .setUsage(C.USAGE_MEDIA)
            .build()
    }

    @Singleton
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

    @Singleton
    @Provides
    fun provideSongRepository(
        @ApplicationContext context: Context
    ) = SongRepository(context = context)

    @Singleton
    @Provides
    fun provideAlbumRepository(
        @ApplicationContext context: Context
    ) = AlbumRepository(context = context)

    @Singleton
    @Provides
    fun provideLyricRepository(lyricDao: LyricDao) =
        LyricRepository(lyricDao = lyricDao)

    @Singleton
    @Provides
    fun provideMediaItemTree(
        albumRepository: AlbumRepository,
        songRepository: SongRepository,
        favoriteRepository: FavoriteRepository
    ): MediaItemTree =
        MediaItemTreeImpl(albumRepository, songRepository, favoriteRepository)

    @Provides
    fun provideFavoriteDao(db: MusicalDatabase) =
        db.favoriteDao()
}
