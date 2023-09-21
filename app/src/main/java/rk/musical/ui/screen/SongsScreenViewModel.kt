package rk.musical.ui.screen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
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

    fun startToCollectSongs() {
        viewModelScope.launch {
            remoteControl.browserEvent.collectLatest {
                if (it is MusicalRemoteControl.BrowserEvent.Connected)
                    loadSongs()
            }
        }
    }


    private suspend fun loadSongs() {
        uiState = SongsScreenUiState.Loading
        val songs = remoteControl.getSongsMediaItems()?.toSongs()
        remoteControl.setPlaylist(songs?.toMediaItems() ?: emptyList())
        uiState = if (songs == null) {
            SongsScreenUiState.Empty
        } else {
            SongsScreenUiState.Loaded(songs)
        }
    }


    fun playSong(index: Int) {
        remoteControl.playSongFromIndex(index)
    }


}

sealed interface SongsScreenUiState {
    data class Loaded(val songs: List<Song>) : SongsScreenUiState
    object Loading : SongsScreenUiState
    object Empty : SongsScreenUiState
}
