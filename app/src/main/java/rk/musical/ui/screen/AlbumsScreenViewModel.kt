package rk.musical.ui.screen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.launch
import rk.musical.MusicalApplication
import rk.musical.data.AlbumRepository
import rk.musical.data.model.Album

class AlbumsScreenViewModel(private val albumRepository: AlbumRepository) : ViewModel() {
    var uiState: AlbumsScreenUiState by mutableStateOf(AlbumsScreenUiState.Empty)
        private set

    init {
        loadAlbums()
    }


    private fun loadAlbums() {
        uiState = AlbumsScreenUiState.Loading
        viewModelScope.launch {
            val albums = albumRepository.loadAlbums()
            uiState = AlbumsScreenUiState.Loaded(albums)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = checkNotNull(extras[APPLICATION_KEY])
                return AlbumsScreenViewModel(albumRepository = AlbumRepository(application as MusicalApplication)) as T
            }
        }
    }
}

sealed interface AlbumsScreenUiState {
    data class Loaded(val albums: List<Album>) : AlbumsScreenUiState
    object Loading : AlbumsScreenUiState
    object Empty : AlbumsScreenUiState
}
