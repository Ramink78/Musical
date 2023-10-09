package rk.musical.ui.screen

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import rk.musical.data.AlbumRepository
import rk.musical.data.model.Album
import rk.musical.data.model.Song
import rk.musical.player.MusicalRemote
import javax.inject.Inject

@HiltViewModel
class AlbumsScreenViewModel @Inject constructor(
    private val musicalRemote: MusicalRemote,
    private val albumRepository: AlbumRepository
) : ViewModel() {
    var uiState: AlbumsScreenUiState by mutableStateOf(AlbumsScreenUiState.Empty)
        private set
    var albums: List<Album> by mutableStateOf(emptyList())
    var albumChildren: List<Song> by mutableStateOf(emptyList())
    private var currentAlbums = emptyList<Album>()
    private var hasCurrentPlaylist = false

    init {
        viewModelScope.launch {
            uiState = AlbumsScreenUiState.Loading
            albumRepository.localAlbums
                .stateIn(scope = viewModelScope)
                .collect {
                    albums = it
                    uiState = if (it.isEmpty())
                        AlbumsScreenUiState.Empty
                    else {
                        currentAlbums = it
                        AlbumsScreenUiState.Loaded
                    }
                }
        }
    }

    fun refreshAlbums() {
        viewModelScope.launch {
            albumRepository.loadAlbums()
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