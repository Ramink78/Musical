package rk.musical.ui.screen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import rk.musical.data.model.Song
import rk.musical.data.model.toSong
import rk.musical.player.MusicalServiceConnection
import rk.musical.utils.SONG_DURATION
import rk.musical.utils.readableDuration

class NowPlayingScreenViewModel(
    private val musicalServiceConnection: MusicalServiceConnection
) : ViewModel() {
    var uiState by mutableStateOf(NowPlayingUiState())
        private set

    private val playbackState = musicalServiceConnection.musicalPlaybackState

    fun skipToNext() {
    }

    fun skipToPrevious() {}
    fun resume() {
        musicalServiceConnection.resume()
    }

    fun pause() {
        musicalServiceConnection.pause()
    }

    fun seekTo(progress: Float) {
        musicalServiceConnection.seekTo(progress)
        uiState = uiState.copy(progress = progress)
    }

    init {
        syncWithPlaybackService()
    }

    private fun syncWithPlaybackService() {
        viewModelScope.launch {
            playbackState.collectLatest { state ->
                if (state.isConnected) {
                    val totalMillis = state.playingMediaItem.mediaMetadata.extras!!.getLong(
                        SONG_DURATION, 0
                    )
                    uiState = uiState.copy(
                        isPlaying = state.isPlaying,
                        playingSong = state.playingMediaItem.toSong(),
                        remainingTime = readableDuration(totalMillis - state.currentPosition),
                        progress = state.currentPosition.toFloat() / totalMillis
                    )
                }
            }
        }
    }

    fun toggleFullScreen() {
        uiState = uiState.copy(isFullScreen = !uiState.isFullScreen)
    }


    class Factory(private val musicalServiceConnection: MusicalServiceConnection) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return NowPlayingScreenViewModel(musicalServiceConnection) as T
        }
    }


}

data class NowPlayingUiState(
    val isPlaying: Boolean = false,
    val playingSong: Song = Song.Empty,
    val isFullScreen: Boolean = false,
    val remainingTime: String = "",
    val progress: Float = 0f
)

