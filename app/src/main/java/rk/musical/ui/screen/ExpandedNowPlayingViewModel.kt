package rk.musical.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.Player
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import rk.musical.data.LyricRepository
import rk.musical.data.model.Lyric
import rk.musical.data.model.Song
import rk.musical.player.MusicalRemote
import rk.musical.utils.readableDuration

@HiltViewModel
class ExpandedNowPlayingViewModel
@Inject
constructor(
    private val musicalRemote: MusicalRemote,
    private val lyricRepository: LyricRepository
) : ViewModel() {
    private val _currentLyric = MutableStateFlow<Lyric?>(null)
    val currentLyric = _currentLyric.asStateFlow()

    init {
        viewModelScope.launch {
            musicalRemote.playingSongFlow.collectLatest {
                fetchLyric(it.id)
            }
        }
    }

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
            .map {
                ExpandedNowPlayingUiState(
                    currentSong = it.currentSong,
                    currentTime = readableDuration(it.currentPosition),
                    totalTime = readableDuration(it.currentSong.duration),
                    isPlaying = it.isPlaying,
                    playbackPosition = it.currentPosition
                )
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                initialValue = ExpandedNowPlayingUiState()
            )
    private val _lyricUiState = MutableStateFlow(LyricUiState())
    val lyricUiState = _lyricUiState.asStateFlow()

    fun fetchLyric(songId: String) {
        viewModelScope.launch {
            val lyric = lyricRepository.getLyricBySongId(songId)
            _currentLyric.value = lyric
        }
    }

    fun showEditorLyric() {
        _lyricUiState.update { it.copy(isEditMode = true) }
    }

    fun hideEditorLyric() {
        _lyricUiState.update { it.copy(isEditMode = false) }
    }

    fun showLyricCover() {
        _lyricUiState.update { it.copy(isVisibleLyric = true) }
    }

    fun hideLyricCover() {
        _lyricUiState.update { it.copy(isVisibleLyric = false) }
    }

    fun submitLyric(lyric: Lyric) {
        viewModelScope.launch {
            lyricRepository.addLyric(
                lyric
            )
            fetchLyric(lyric.songId)
        }
    }

    fun skipToNext() = musicalRemote.seekNext()

    fun skipToPrevious() = musicalRemote.seekPrevious()

    fun togglePlay() = musicalRemote.togglePlay()

    fun toggleShuffleMode() = musicalRemote.setShuffleMode(!shuffleModeFlow.value)

    fun seekToProgress(pos: Long) {
        musicalRemote.seekToPosition(pos)
    }

    fun changeRepeatMode() {
        when (repeatModeFlow.value) {
            Player.REPEAT_MODE_OFF -> musicalRemote.setRepeatMode(Player.REPEAT_MODE_ALL)
            Player.REPEAT_MODE_ALL -> musicalRemote.setRepeatMode(Player.REPEAT_MODE_ONE)
            else -> musicalRemote.setRepeatMode(Player.REPEAT_MODE_OFF)
        }
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
    val isPlaying: Boolean = false,
    val playbackPosition: Long = 0L
)

data class LyricUiState(
    val isEditMode: Boolean = false,
    val isVisibleLyric: Boolean = false
)
