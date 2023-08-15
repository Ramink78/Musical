package rk.musical.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.Player
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import rk.musical.player.MusicalPlaybackState
import rk.musical.player.MusicalServiceConnection
import javax.inject.Inject

@HiltViewModel
class MusicalAppViewModel @Inject constructor(
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
}