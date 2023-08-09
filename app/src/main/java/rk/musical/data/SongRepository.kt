package rk.musical.data

import android.content.ContentUris
import android.content.Context
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
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
    override val isReady: Boolean
        get() {
            return state is DataSourceState.Success
        }

    private suspend fun loadSongs(
        sortOrder: String = SortOrder.Descending.dateAdded,
    ) {
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
                val albumIdCol = cursor.albumIdColumnIndex
                val albumNameCol = cursor.albumNameColumnIndex
                val songDurationCol = cursor.songDurationColumnIndex
                chacedSongs = buildList {
                    while (cursor.moveToNext()) {
                        val albumId = cursor.getLong(albumIdCol)
                        val songId = cursor.getLong(idCol)
                        val songUri = ContentUris.withAppendedId(SONGS_URI, songId)
                        add(
                            Song(
                                id = songId.toString(),
                                title = cursor.getString(titleCol),
                                artist = cursor.getString(artistCol),
                                albumId = albumId.toString(),
                                songUri = songUri.toString(),
                                albumName = cursor.getString(albumNameCol),
                                coverUri = buildCoverUri(albumId),
                                duration = cursor.getLong(songDurationCol)
                            ),
                        )
                    }
                }

            }
        }

    }

    private fun buildCoverUri(albumId: Long): String {
        return "content://media/external/audio/media/$albumId/albumart"
    }

    override suspend fun load() {
        if (isReady) return
        state = DataSourceState.Loading
        loadSongs()
        state = DataSourceState.Success
    }

    override fun iterator(): Iterator<Song> = chacedSongs.iterator()
}