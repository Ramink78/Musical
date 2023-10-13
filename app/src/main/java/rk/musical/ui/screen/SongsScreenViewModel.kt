package rk.musical.ui.screen

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import rk.musical.data.SongRepository
import rk.musical.data.model.Song
import rk.musical.player.MusicalRemote
import javax.inject.Inject

@HiltViewModel
class SongsScreenViewModel
    @Inject
    constructor(
        private val songRepository: SongRepository,
        private val musicalRemote: MusicalRemote,
    ) : ViewModel() {
        var uiState: SongsScreenUiState by mutableStateOf(SongsScreenUiState.Empty)
            private set

        private var currentSongs = emptyList<Song>()

        // private var hasCurrentPlaylist = false
        val playingSongFlow =
            musicalRemote.playingSongFlow
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5000),
                    initialValue = Song.Empty,
                )

        init {
            Log.i(SongsScreenViewModel::class.simpleName, "Created ViewModel")
            viewModelScope.launch {
                uiState = SongsScreenUiState.Loading
                songRepository.localSongs
                    .stateIn(scope = viewModelScope)
                    .collect {
                        uiState =
                            if (it.isEmpty()) {
                                SongsScreenUiState.Empty
                            } else {
                                currentSongs = it
                                SongsScreenUiState.Loaded(it)
                            }
                    }
            }
        }

        fun refreshSongs() {
            viewModelScope.launch {
                songRepository.loadSongs()
            }
        }

        fun playSong(index: Int) {
            if (musicalRemote.currentPlaylist != currentSongs) {
                musicalRemote.setPlaylist(currentSongs)
                //    hasCurrentPlaylist = true
            }
            musicalRemote.playSong(index)
        }
    }

sealed interface SongsScreenUiState {
    data class Loaded(val songs: List<Song>) : SongsScreenUiState
    object Loading : SongsScreenUiState
    object Empty : SongsScreenUiState
}
