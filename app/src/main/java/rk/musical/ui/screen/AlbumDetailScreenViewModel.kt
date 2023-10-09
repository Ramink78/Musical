package rk.musical.ui.screen

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import rk.musical.data.AlbumRepository
import rk.musical.data.SongRepository
import rk.musical.data.model.Song
import javax.inject.Inject

@HiltViewModel
class AlbumDetailScreenViewModel @Inject constructor(
    private val albumRepository: AlbumRepository,
    private val songRepository: SongRepository
) : ViewModel() {
    fun findAlbumById(id: String) = albumRepository.cachedAlbums.find { it.id == id }
    fun getAlbumChildren(albumId: String): List<Song> {
        val album = findAlbumById(albumId) ?: return emptyList()
        return songRepository.getAlbumSongs(album.title)
    }

}