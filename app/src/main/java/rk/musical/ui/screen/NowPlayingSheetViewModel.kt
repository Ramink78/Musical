package rk.musical.ui.screen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import rk.musical.data.model.Song
import rk.musical.data.model.toSong
import rk.musical.player.MusicalRemoteControl
import rk.musical.utils.readableDuration
import javax.inject.Inject

@HiltViewModel
class NowPlayingScreenViewModel @Inject constructor(
    private val remoteControl: MusicalRemoteControl
) : ViewModel() {
    var isDragging = false


    var uiState by mutableStateOf(NowPlayingUiState())
        private set

    private val playbackState = remoteControl.musicalPlaybackState

    fun updateProgress(progress: Float) {
        isDragging = true
        uiState = uiState.copy(
            progress = progress,
            currentTime = readableDuration((progress * uiState.playingSong.duration).toLong())
        )
    }

    fun skipToNext() {
        remoteControl.skipNext()
    }

    fun skipToPrevious() {
        remoteControl.skipPrevious()
    }

    private fun resume() {
        remoteControl.resume()
    }

    private fun pause() {
        remoteControl.pause()
    }

    fun togglePlay() {
        if (uiState.isPlaying)
            pause()
        else resume()
    }

    fun seekTo(progress: Float) {
        isDragging = false
        remoteControl.seekTo(progress)
        uiState = uiState.copy(
            progress = progress,
            currentTime = readableDuration((progress * uiState.playingSong.duration).toLong())
        )
    }

    init {
        syncWithPlaybackService()
    }

    private fun syncWithPlaybackService() {
        viewModelScope.launch {
            playbackState.combine(remoteControl.playbackPosition) { state, position ->
                val playingSong = state.playingMediaItem.toSong()
                val totalDuration = uiState.playingSong.duration
                val progress = position.toFloat() / totalDuration
                val remainingTime = readableDuration(position)
                NowPlayingUiState(
                    isReady = state.isReady,
                    isPlaying = state.isPlaying,
                    playingSong = playingSong,
                    totalDuration = playingSong.duration,
                    progress = progress,
                    currentTime = remainingTime

                )
            }.collect {
                    uiState = it
            }
        }
    }
}

data class NowPlayingUiState(
    val isReady: Boolean = false,
    val isPlaying: Boolean = false,
    val playingSong: Song = Song.Empty,
    val isFullScreen: Boolean = false,
    val currentTime: String = "00:00",
    val isVisible: Boolean = false,
    val progress: Float = 0f,
    val totalDuration: Long = 0L
)

