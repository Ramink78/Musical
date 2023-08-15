package rk.musical.ui.screen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import rk.musical.data.model.Song
import rk.musical.data.model.toSong
import rk.musical.player.MusicalServiceConnection
import rk.musical.utils.readableDuration
import javax.inject.Inject

@HiltViewModel
class NowPlayingScreenViewModel @Inject constructor(
    private val musicalServiceConnection: MusicalServiceConnection
) : ViewModel() {

    private var needToUpdatePosition: Boolean = true
        set(value) {
            field = value
            if (value)
                updateProgressAndTime()
        }


    var uiState by mutableStateOf(NowPlayingUiState())
        private set

    private val playbackState = musicalServiceConnection.musicalPlaybackState

    fun updateProgress(progress: Float) {
        needToUpdatePosition = false
        uiState = uiState.copy(
            progress = progress,
            currentTime = readableDuration((progress * uiState.playingSong.duration).toLong())
        )
    }

    fun skipToNext() {
    }

    fun skipToPrevious() {}
    fun resume() {
        needToUpdatePosition = true && uiState.isFullScreen
        musicalServiceConnection.resume()
    }

    fun pause() {
        needToUpdatePosition = false
        musicalServiceConnection.pause()
    }

    fun seekTo(progress: Float) {
        needToUpdatePosition = true
        musicalServiceConnection.seekTo(progress)
        uiState = uiState.copy(
            progress = progress,
            currentTime = readableDuration((progress * uiState.playingSong.duration).toLong())
        )
    }

    init {
        syncWithPlaybackService()
        updateProgressAndTime()
    }

    private fun syncWithPlaybackService() {
        viewModelScope.launch {
            playbackState.collectLatest { state ->
                if (state.isConnected) {
                    uiState = uiState.copy(
                        isPlaying = state.isPlaying,
                        playingSong = state.playingMediaItem.toSong(),
                    )
                }
            }
        }
    }

    fun toggleFullScreen() {
        uiState = uiState.copy(isFullScreen = !uiState.isFullScreen)
        needToUpdatePosition = uiState.isFullScreen
    }


    private fun updateProgressAndTime() {
        viewModelScope.launch {
            while (true) {
                if (!needToUpdatePosition || !uiState.isFullScreen) return@launch
                val currentPosition = musicalServiceConnection.currentPosition
                val totalDuration = uiState.playingSong.duration
                val remainingTime = readableDuration(currentPosition)
                uiState = uiState.copy(
                    progress = currentPosition.toFloat() / totalDuration,
                    currentTime = remainingTime
                )
                delay(1000)
            }
        }

    }

    override fun onCleared() {
        super.onCleared()
        needToUpdatePosition = false
    }



}

data class NowPlayingUiState(
    val isPlaying: Boolean = false,
    val playingSong: Song = Song.Empty,
    val isFullScreen: Boolean = false,
    val currentTime: String = "",
    val progress: Float = 0f
)

