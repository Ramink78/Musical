package rk.musical.ui.screen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.launch
import rk.musical.data.model.Album
import rk.musical.data.model.toAlbums
import rk.musical.player.MusicalServiceConnection

class AlbumsScreenViewModel(
    private val musicalServiceConnection: MusicalServiceConnection
) : ViewModel() {
    var uiState: AlbumsScreenUiState by mutableStateOf(AlbumsScreenUiState.Empty)
        private set

    init {
        collectMusicalPlaybackState()
    }

    private fun collectMusicalPlaybackState() {
        viewModelScope.launch {
            musicalServiceConnection.musicalPlaybackState.collect {
                if (it.isConnected) {
                    loadAlbums()
                }
            }
        }
    }


    private fun loadAlbums() {
        uiState = AlbumsScreenUiState.Loading
        viewModelScope.launch {
            val albums = musicalServiceConnection.getAlbumsMediaItems()
            uiState = AlbumsScreenUiState.Loaded(
                albums = albums?.toAlbums() ?: emptyList()
            )
        }
    }

    class Factory(private val musicalServiceConnection: MusicalServiceConnection) :
        ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
            return AlbumsScreenViewModel(musicalServiceConnection = musicalServiceConnection) as T
        }
    }
}


sealed interface AlbumsScreenUiState {
    data class Loaded(val albums: List<Album>) : AlbumsScreenUiState
    object Loading : AlbumsScreenUiState
    object Empty : AlbumsScreenUiState
}