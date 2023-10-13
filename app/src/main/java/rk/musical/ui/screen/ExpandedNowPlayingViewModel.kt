package rk.musical.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.Player
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.math.roundToLong
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import rk.musical.data.model.Song
import rk.musical.player.MusicalRemote
import rk.musical.utils.readableDuration

@HiltViewModel
class ExpandedNowPlayingViewModel
@Inject
constructor(
    private val musicalRemote: MusicalRemote
) : ViewModel() {
    private val _uiProgress = MutableStateFlow(0f)
    val uiProgress = _uiProgress.asStateFlow()
    val repeatModeFlow =
        musicalRemote.repeatModeFlow
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = 0
            )
    val shuffleModeFlow =
        musicalRemote.shuffleModeFlow
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = false
            )
    val nowPlayingUiStateFlow =
        musicalRemote.playbackStateFlow
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

    fun toggleShuffleMode() = musicalRemote.setShuffleMode(!shuffleModeFlow.value)

    fun seekToProgress(progress: Float) {
        val duration = nowPlayingUiStateFlow.value.currentSong.duration
        isSeeking = false
        musicalRemote.seekToPosition((progress * duration).roundToLong())
    }

    fun changeRepeatMode() {
        when (repeatModeFlow.value) {
            Player.REPEAT_MODE_OFF -> musicalRemote.setRepeatMode(Player.REPEAT_MODE_ALL)
            Player.REPEAT_MODE_ALL -> musicalRemote.setRepeatMode(Player.REPEAT_MODE_ONE)
            else -> musicalRemote.setRepeatMode(Player.REPEAT_MODE_OFF)
        }
    }

    fun updateProgress(progress: Float) {
        isSeeking = true
        _uiProgress.value = progress
    }

    fun setPlaybackSpeed(index: Int) {
        when (index) {
            0 -> musicalRemote.setPlaybackSpeed(.5f)
            1 -> musicalRemote.setPlaybackSpeed(1f)
            2 -> musicalRemote.setPlaybackSpeed(1.5f)
            else -> musicalRemote.setPlaybackSpeed(2f)
        }
    }
}

data class ExpandedNowPlayingUiState(
    val currentSong: Song = Song.Empty,
    val totalTime: String = "00:00",
    val currentTime: String = "00:00",
    val isPlaying: Boolean = false
)
