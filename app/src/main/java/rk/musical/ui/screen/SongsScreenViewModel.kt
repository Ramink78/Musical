package rk.musical.ui.screen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import rk.musical.data.model.Song
import rk.musical.data.model.toSongs
import rk.musical.player.MusicalServiceConnection
import javax.inject.Inject

@HiltViewModel
class SongsScreenViewModel @Inject constructor(
    private val musicalServiceConnection: MusicalServiceConnection,
) : ViewModel() {
    var uiState: SongsScreenUiState by mutableStateOf(SongsScreenUiState.Empty)
        private set
    private var isLoadedSongs = false

    init {
        collectMusicalPlaybackState()
    }

    private fun collectMusicalPlaybackState() {
        viewModelScope.launch {
            musicalServiceConnection.musicalPlaybackState.collect {
                if (it.isConnected && uiState !is SongsScreenUiState.Loaded)
                    loadSongs()
            }
        }
    }


    private fun loadSongs() {
        uiState = SongsScreenUiState.Loading
        viewModelScope.launch {
            val songs = musicalServiceConnection.getSongsMediaItems()?.toSongs()
            uiState = if (songs == null) {
                SongsScreenUiState.Empty
            } else {
                SongsScreenUiState.Loaded(songs)
            }
            isLoadedSongs = true
        }
    }

    fun playSong(song: Song) {
        musicalServiceConnection.playSong(song)
    }


}

sealed interface SongsScreenUiState {
    data class Loaded(val songs: List<Song>) : SongsScreenUiState
    object Loading : SongsScreenUiState
    object Empty : SongsScreenUiState
}
