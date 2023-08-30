package rk.musical.ui.screen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import rk.musical.data.model.Song
import rk.musical.data.model.toMediaItems
import rk.musical.data.model.toSongs
import rk.musical.player.MusicalRemoteControl
import javax.inject.Inject

@HiltViewModel
class SongsScreenViewModel @Inject constructor(
    private val remoteControl: MusicalRemoteControl,
) : ViewModel() {
    var uiState: SongsScreenUiState by mutableStateOf(SongsScreenUiState.Empty)
        private set
    private var isLoadedSongs = false

    init {
        collectMusicalPlaybackState()
    }

    private fun collectMusicalPlaybackState() {
        viewModelScope.launch {
            remoteControl.musicalPlaybackState.collect {
                if (it.isConnected && uiState !is SongsScreenUiState.Loaded)
                    loadSongs()
            }
        }
    }


    private fun loadSongs() {
        uiState = SongsScreenUiState.Loading
        viewModelScope.launch {
            val songs = remoteControl.getSongsMediaItems()?.toSongs()
            remoteControl.setPlaylist(songs?.toMediaItems() ?: emptyList())
            uiState = if (songs == null) {
                SongsScreenUiState.Empty
            } else {
                isLoadedSongs = true
                SongsScreenUiState.Loaded(songs)
            }
        }
    }

    fun playSong(index: Int) {
        remoteControl.playSongFromIndex(index)
    }

    override fun onCleared() {
        super.onCleared()
        remoteControl.releaseControl()
    }


}

sealed interface SongsScreenUiState {
    data class Loaded(val songs: List<Song>) : SongsScreenUiState
    object Loading : SongsScreenUiState
    object Empty : SongsScreenUiState
}
