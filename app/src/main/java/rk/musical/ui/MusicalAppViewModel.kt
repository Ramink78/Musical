package rk.musical.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.media3.common.Player
import kotlinx.coroutines.launch
import rk.musical.player.MusicalPlaybackState
import rk.musical.player.MusicalServiceConnection

class MusicalAppViewModel(
    private val musicalServiceConnection: MusicalServiceConnection,
) : ViewModel(), Player.Listener {

    var musicalPlaybackState: MusicalPlaybackState by mutableStateOf(MusicalPlaybackState())
        private set

    init {
        viewModelScope.launch {
            musicalServiceConnection.musicalPlaybackState.collect {
                musicalPlaybackState = it
            }
        }
    }

    companion object {
        @Suppress("UNCHECKED_CAST")
        class Factory(private val musicalServiceConnection: MusicalServiceConnection) :
            ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return MusicalAppViewModel(musicalServiceConnection) as T
            }
        }
    }
}