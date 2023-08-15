package rk.musical.data

import rk.musical.data.model.Song

interface LocalSongsDataSource : Iterable<Song> {
    val isReady: Boolean
    suspend fun load()
    fun getAlbumSongs(albumName: String): List<Song>

}
