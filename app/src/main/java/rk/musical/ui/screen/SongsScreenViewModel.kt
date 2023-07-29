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
import rk.musical.data.SongRepository
import rk.musical.data.model.Song

class SongsScreenViewModel(private val songsRepository: SongRepository) : ViewModel() {
    var uiState: SongsScreenUiState by mutableStateOf(SongsScreenUiState.Empty)
        private set

    init {
        loadSongs()
    }


    private fun loadSongs() {
        uiState = SongsScreenUiState.Loading
        viewModelScope.launch {
            val songs = songsRepository.loadSongs()
            uiState = SongsScreenUiState.Loaded(songs)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = checkNotNull(extras[APPLICATION_KEY])
                return SongsScreenViewModel(songsRepository = SongRepository(application as MusicalApplication)) as T
            }
        }
    }
}
sealed interface SongsScreenUiState {
    data class Loaded(val songs: List<Song>) : SongsScreenUiState
    object Loading : SongsScreenUiState
    object Empty : SongsScreenUiState
}
