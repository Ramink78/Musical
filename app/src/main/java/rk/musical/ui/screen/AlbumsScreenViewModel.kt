package rk.musical.ui.screen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import rk.musical.data.model.Album
import rk.musical.data.model.Song
import rk.musical.data.model.toAlbums
import rk.musical.data.model.toSongs
import rk.musical.player.MusicalRemoteControl
import javax.inject.Inject

@HiltViewModel
class AlbumsScreenViewModel @Inject constructor(
    private val remoteControl: MusicalRemoteControl
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
            remoteControl.musicalPlaybackState.collect {
                if (it.isConnected && uiState is AlbumsScreenUiState.Loading) {
                    loadAlbums()
                }
            }
        }
    }


    private fun loadAlbums() {
        viewModelScope.launch {
            val loadedAlbums = remoteControl.getAlbumsMediaItems()
            uiState = AlbumsScreenUiState.Loaded
            albums = loadedAlbums?.toAlbums() ?: emptyList()
            if (albums.isEmpty())
                uiState = AlbumsScreenUiState.Empty

        }
    }

    fun play(song: Song) {
        remoteControl.playSong(song)
    }

    fun navigateBackToAlbums() {
        uiState = AlbumsScreenUiState.NavigateBack
    }

    fun loadAlbumChildren(album: Album) {
        viewModelScope.launch {
            uiState = AlbumsScreenUiState.Loading
            val childrenMediaItems = remoteControl.getAlbumChild(album.id) ?: emptyList()
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