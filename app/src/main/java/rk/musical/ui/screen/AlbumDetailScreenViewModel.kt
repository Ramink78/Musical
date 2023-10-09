package rk.musical.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import rk.musical.data.AlbumRepository
import rk.musical.data.SongRepository
import rk.musical.data.model.Song
import rk.musical.player.MusicalRemote
import javax.inject.Inject

@HiltViewModel
class AlbumDetailScreenViewModel @Inject constructor(
    private val albumRepository: AlbumRepository,
    private val songRepository: SongRepository,
    private val musicalRemote: MusicalRemote
) : ViewModel() {
    private var hasCurrentPlaylist = false
    private var currentSongs = emptyList<Song>()
    val playingSong = musicalRemote.playingSongFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Song.Empty
        )

    fun findAlbumById(id: String) = albumRepository.cachedAlbums.find { it.id == id }
    fun getAlbumChildren(albumId: String): List<Song> {
        val album = findAlbumById(albumId) ?: return emptyList()
        currentSongs = songRepository.getAlbumSongs(album.title)
        return currentSongs
    }

    fun playSong(index: Int) {
        if (!hasCurrentPlaylist) {
            musicalRemote.setPlaylist(currentSongs)
            hasCurrentPlaylist = true
        }
        musicalRemote.playSong(index)
    }

}