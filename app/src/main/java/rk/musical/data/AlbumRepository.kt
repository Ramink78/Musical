package rk.musical.data

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.withContext
import rk.musical.data.model.Album
import rk.musical.utils.ALBUMS_URI
import rk.musical.utils.albumArtColumnIndex
import rk.musical.utils.albumColumns
import rk.musical.utils.albumIdColumnIndex
import rk.musical.utils.albumNameColumnIndex
import rk.musical.utils.albumSongsCountColumnIndex
import rk.musical.utils.artistColumnIndex
import rk.musical.utils.kuery

class AlbumRepository(
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val context: Context
) : AlbumDataSource {
    private var state: DataSourceState = DataSourceState.Created
    override val isReady: Boolean
        get() {
            return state is DataSourceState.Success
        }
    var cachedAlbums: List<Album> = listOf()
        private set
    private val _localAlbums = MutableSharedFlow<List<Album>>()
    val localAlbums = _localAlbums.asSharedFlow()

    suspend fun loadAlbums() {
        withContext(dispatcher) {
            context.contentResolver.kuery(
                uri = ALBUMS_URI,
                columns = albumColumns
            )?.use { it ->
                val albumNameCol = it.albumNameColumnIndex
                val albumIdCol = it.albumIdColumnIndex
                val albumArtCol = it.albumArtColumnIndex
                val artistCol = it.artistColumnIndex
                val albumSongsCountCol = it.albumSongsCountColumnIndex
                val tempList = mutableListOf<Album>()

                while (it.moveToNext()) {
                    val albumId = it.getLong(albumIdCol)
                    val sArtworkUri =
                        Uri
                            .parse("content://media/external/audio/albumart")
                    val albumArtUri = ContentUris.withAppendedId(sArtworkUri, albumId)
                    tempList.add(
                        Album(
                            id = albumId.toString(),
                            title = it.getString(albumNameCol),
                            artist = it.getString(artistCol),
                            songsCount = it.getInt(albumSongsCountCol),
                            coverUri = albumArtUri.toString()
                        )
                    )
                }
                cachedAlbums = tempList
                _localAlbums.emit(tempList)
            }
        }
    }

    override suspend fun load() {
        if (isReady) return
        state = DataSourceState.Loading
        loadAlbums()
        state = DataSourceState.Success
    }

    override fun iterator(): Iterator<Album> = cachedAlbums.iterator()
}
