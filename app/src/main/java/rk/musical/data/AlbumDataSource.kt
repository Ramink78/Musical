package rk.musical.data

import rk.musical.data.model.Album


interface AlbumDataSource : Iterable<Album> {
    val isReady: Boolean
    suspend fun load()
}



