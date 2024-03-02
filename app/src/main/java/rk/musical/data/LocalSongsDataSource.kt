package rk.musical.data

import rk.musical.data.model.Song

interface LocalSongsDataSource {
    val isReady: Boolean

    suspend fun load()

    fun getAlbumSongs(albumId: String): List<Song>
}
