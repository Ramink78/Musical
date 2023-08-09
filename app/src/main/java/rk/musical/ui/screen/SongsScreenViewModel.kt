package rk.musical.ui.screen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.launch
import rk.musical.data.model.Song
import rk.musical.data.model.toSongs
import rk.musical.player.MusicalServiceConnection

class SongsScreenViewModel(
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

    class Factory(
        private val musicalServiceConnection: MusicalServiceConnection,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
            return SongsScreenViewModel(musicalServiceConnection = musicalServiceConnection) as T
        }
    }

}

sealed interface SongsScreenUiState {
    data class Loaded(val songs: List<Song>) : SongsScreenUiState
    object Loading : SongsScreenUiState
    object Empty : SongsScreenUiState
}
