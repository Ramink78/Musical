package rk.musical.data

import android.content.ContentUris
import android.content.Context
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.withContext
import rk.musical.data.model.Song
import rk.musical.utils.IS_MUSIC_CLAUSE
import rk.musical.utils.SONGS_URI
import rk.musical.utils.SortOrder
import rk.musical.utils.albumIdColumnIndex
import rk.musical.utils.albumNameColumnIndex
import rk.musical.utils.artistColumnIndex
import rk.musical.utils.kuery
import rk.musical.utils.songColumns
import rk.musical.utils.songDurationColumnIndex
import rk.musical.utils.songIdColumnIndex
import rk.musical.utils.songNameColumnIndex

class SongRepository(
    private val context: Context,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : LocalSongsDataSource {
    var chacedSongs: List<Song> = emptyList()
        private set
    private var state: DataSourceState = DataSourceState.Created
    private val _localSongs = MutableSharedFlow<List<Song>>()
    val localSongs = _localSongs.asSharedFlow()

    override val isReady: Boolean
        get() {
            return state is DataSourceState.Success
        }

    suspend fun loadSongs(sortOrder: String = SortOrder.Descending.dateAdded) {
        withContext(dispatcher) {
            context.contentResolver.kuery(
                uri = SONGS_URI,
                columns = songColumns,
                sortOrder = sortOrder,
                selection = IS_MUSIC_CLAUSE
            )?.use { cursor ->
                val idCol = cursor.songIdColumnIndex
                val titleCol = cursor.songNameColumnIndex
                val artistCol = cursor.artistColumnIndex
                val albumNameCol = cursor.albumNameColumnIndex
                val songDurationCol = cursor.songDurationColumnIndex
                val albumIdCol = cursor.albumIdColumnIndex
                val tempList = mutableListOf<Song>()
                while (cursor.moveToNext()) {
                    val songId = cursor.getLong(idCol)
                    val songUri = ContentUris.withAppendedId(SONGS_URI, songId)
                    val albumId = cursor.getLong(albumIdCol).toString()
                    tempList.add(
                        Song(
                            id = songId.toString(),
                            title = cursor.getString(titleCol),
                            artist = cursor.getString(artistCol),
                            songUri = songUri.toString(),
                            albumName = cursor.getString(albumNameCol),
                            coverUri = buildCoverUri(songId),
                            duration = cursor.getLong(songDurationCol),
                            albumId = albumId
                        )
                    )
                }
                chacedSongs = tempList
                _localSongs.emit(tempList)
            }
        }
    }

    private fun buildCoverUri(id: Long): String {
        return "content://media/external/audio/media/$id/albumart"
    }

    override suspend fun load() {
        if (isReady) return
        state = DataSourceState.Loading
        loadSongs()
        state = DataSourceState.Success
    }

    override fun getAlbumSongs(albumId: String): List<Song> {
        return chacedSongs.filter { it.albumId == albumId }
    }
}
