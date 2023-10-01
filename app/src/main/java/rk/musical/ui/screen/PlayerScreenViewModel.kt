package rk.musical.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import rk.musical.data.model.Song
import rk.musical.player.MusicalRemote
import javax.inject.Inject

@HiltViewModel
class PlayerScreenViewModel @Inject constructor
    (private val musicalRemote: MusicalRemote) : ViewModel() {
    val isVisibleSheetFlow = musicalRemote.playingSongFlow
        .distinctUntilChanged()
        .map {
            it != Song.Empty
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )
}