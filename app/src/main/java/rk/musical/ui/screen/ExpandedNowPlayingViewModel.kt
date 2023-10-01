package rk.musical.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import rk.musical.data.model.Song
import rk.musical.player.MusicalRemote
import rk.musical.utils.readableDuration
import javax.inject.Inject
import kotlin.math.roundToLong

@HiltViewModel
class ExpandedNowPlayingViewModel @Inject constructor(
    private val musicalRemote: MusicalRemote
) : ViewModel() {
    private val _uiProgress = MutableStateFlow(0f)
    val uiProgress = _uiProgress.asStateFlow()

    val nowPlayingUiStateFlow = musicalRemote.playbackStateFlow
        .distinctUntilChanged()
        .map {
            if (!isSeeking) {
                _uiProgress.value = it.currentPosition.toFloat() / it.currentSong.duration
            }
            ExpandedNowPlayingUiState(
                currentSong = it.currentSong,
                currentTime = readableDuration(it.currentPosition),
                totalTime = readableDuration(it.currentSong.duration),
                isPlaying = it.isPlaying
            )
        }

        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            initialValue = ExpandedNowPlayingUiState()
        )
    private var isSeeking = false


    fun skipToNext() = musicalRemote.seekNext()
    fun skipToPrevious() = musicalRemote.seekPrevious()
    fun togglePlay() = musicalRemote.togglePlay()
    fun seekToProgress() {
        val duration = nowPlayingUiStateFlow.value.currentSong.duration
        isSeeking = false
        musicalRemote.seekToPosition((uiProgress.value * duration).roundToLong())
    }

    fun updateProgress(progress: Float) {
        isSeeking = true
        _uiProgress.value = progress
    }
}


data class ExpandedNowPlayingUiState(
    val currentSong: Song = Song.Empty,
    val totalTime: String = "00:00",
    val currentTime: String = "00:00",
    val isPlaying: Boolean = false
)

