package rk.musical.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import rk.musical.data.AlbumRepository
import rk.musical.data.SongRepository
import rk.musical.data.model.Song
import rk.musical.player.MusicalRemote

@HiltViewModel
class AlbumDetailScreenViewModel
@Inject
constructor(
    private val albumRepository: AlbumRepository,
    private val songRepository: SongRepository,
    private val musicalRemote: MusicalRemote
) : ViewModel() {
    private var currentSongs = emptyList<Song>()
    val playingSong =
        musicalRemote.playingSongFlow
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = Song.Empty
            )

    fun findAlbumById(id: String) = albumRepository.cachedAlbums.find { it.id == id }

    fun getAlbumChildren(albumId: String): List<Song> {
        currentSongs = songRepository.getAlbumSongs(albumId)
        return currentSongs
    }

    fun playSong(index: Int) {
        if (musicalRemote.currentPlaylist != currentSongs) {
            musicalRemote.setPlaylist(currentSongs)
            // hasCurrentPlaylist = true
        }
        musicalRemote.playSong(index)
    }
}
