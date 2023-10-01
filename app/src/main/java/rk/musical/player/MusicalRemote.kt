package rk.musical.player

import androidx.media3.exoplayer.ExoPlayer
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.flow.combine
import rk.musical.data.model.Song
import rk.musical.data.model.toMediaItems
import javax.inject.Inject

@ActivityRetainedScoped
class MusicalRemote @Inject constructor(private val exoPlayer: ExoPlayer) {
    val playingSongFlow = exoPlayer.playingSongFlow()
    val isPlayingFlow = exoPlayer.isPlayingFlow()
    val currentPositionFlow = exoPlayer.currentPositionFlow()
    val playbackStateFlow = combine(
        playingSongFlow,
        isPlayingFlow,
        currentPositionFlow
    ) { playingSong, isPlaying, currentPosition ->
        MusicalRemoteState(
            isPlaying = isPlaying,
            currentSong = playingSong,
            currentPosition = currentPosition,
        )
    }

    fun playSong(index: Int) {
        exoPlayer.seekToDefaultPosition(index)
        exoPlayer.playWhenReady = true
    }

    fun setPlaylist(songList: List<Song>) {
        exoPlayer.setMediaItems(songList.toMediaItems())
        exoPlayer.prepare()
    }

    fun pause() {
        exoPlayer.playWhenReady = false
    }

    fun play() {
        exoPlayer.playWhenReady = true
    }

    fun togglePlay() {
        exoPlayer.playWhenReady = !exoPlayer.isPlaying
    }

    fun seekNext() = exoPlayer.seekToNext()
    fun seekPrevious() = exoPlayer.seekToPrevious()
    fun seekToPosition(pos: Long) = exoPlayer.seekTo(pos)

}

data class MusicalRemoteState(
    val isPlaying: Boolean = false,
    val currentSong: Song = Song.Empty,
    val currentPosition: Long = 0L
)
