package rk.musical.ui.screen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import rk.musical.data.model.Album
import rk.musical.data.model.Song
import rk.musical.data.model.logger
import rk.musical.data.model.toAlbums
import rk.musical.data.model.toSongs
import rk.musical.player.MusicalRemoteControl
import javax.inject.Inject

@HiltViewModel
class AlbumsScreenViewModel @Inject constructor(
    private val remoteControl: MusicalRemoteControl
) : ViewModel() {
    var uiState: AlbumsScreenUiState by mutableStateOf(AlbumsScreenUiState.Empty)
        private set
    var albums: List<Album> by mutableStateOf(emptyList())
    var albumChildren: List<Song> by mutableStateOf(emptyList())

    fun startToCollectAlbums() {
        viewModelScope.launch {
            remoteControl.browserEvent.collectLatest {
                if (it is MusicalRemoteControl.BrowserEvent.Connected) {
                    loadAlbums()
                }
            }
        }
    }


    private suspend fun loadAlbums() {
        uiState = AlbumsScreenUiState.Loading
        val loadedAlbums = remoteControl.getAlbumsMediaItems()
        albums = loadedAlbums?.toAlbums() ?: emptyList()
        uiState = AlbumsScreenUiState.Loaded
        if (albums.isEmpty()) {
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