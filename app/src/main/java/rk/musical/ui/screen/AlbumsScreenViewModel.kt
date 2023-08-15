package rk.musical.ui.screen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import rk.musical.data.model.Album
import rk.musical.data.model.Song
import rk.musical.data.model.toAlbums
import rk.musical.data.model.toSongs
import rk.musical.player.MusicalServiceConnection
import javax.inject.Inject

@HiltViewModel
class AlbumsScreenViewModel @Inject constructor(
    private val musicalServiceConnection: MusicalServiceConnection
) : ViewModel() {
    var uiState: AlbumsScreenUiState by mutableStateOf(AlbumsScreenUiState.Loading)
        private set
    var albums: List<Album> by mutableStateOf(emptyList())
    var albumChildren: List<Song> by mutableStateOf(emptyList())

    init {
        collectMusicalPlaybackState()
    }

    private fun collectMusicalPlaybackState() {
        viewModelScope.launch {
            musicalServiceConnection.musicalPlaybackState.collect {
                if (it.isConnected && uiState is AlbumsScreenUiState.Loading) {
                    loadAlbums()
                }
            }
        }
    }


    private fun loadAlbums() {
        viewModelScope.launch {
            val loadedAlbums = musicalServiceConnection.getAlbumsMediaItems()
            uiState = AlbumsScreenUiState.Loaded
            albums = loadedAlbums?.toAlbums() ?: emptyList()
            if (albums.isEmpty())
                uiState = AlbumsScreenUiState.Empty

        }
    }

    fun play(song: Song) {
        musicalServiceConnection.playSong(song)
    }

    fun navigateBackToAlbums() {
        uiState = AlbumsScreenUiState.NavigateBack
    }

    fun loadAlbumChildren(album: Album) {
        viewModelScope.launch {
            uiState = AlbumsScreenUiState.Loading
            val childrenMediaItems = musicalServiceConnection.getAlbumChild(album.id) ?: emptyList()
            uiState =
                if (childrenMediaItems.isNotEmpty()) {
                    AlbumsScreenUiState.LoadedChildren.also {
                        albumChildren = childrenMediaItems.toSongs()
                    }
                } else AlbumsScreenUiState.Empty

        }
    }

}


sealed interface AlbumsScreenUiState {
    object Loaded : AlbumsScreenUiState
    object LoadedChildren : AlbumsScreenUiState
    object Loading : AlbumsScreenUiState
    object NavigateBack : AlbumsScreenUiState
    object Empty : AlbumsScreenUiState
}