package rk.musical.data

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
import rk.musical.utils.songIdColumnIndex
import rk.musical.utils.songNameColumnIndex

class SongRepository(private val context: Context) {


    suspend fun loadSongs(
        sortOrder: String = SortOrder.Descending.dateAdded,
        dispatcher: CoroutineDispatcher = Dispatchers.IO
    ): List<Song> {
        val songs = mutableListOf<Song>()
        return withContext(dispatcher) {
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
                while (cursor.moveToNext()) {
                    val albumId = cursor.getLong(albumIdCol)
                    songs.add(
                        Song(
                            id = cursor.getLong(idCol),
                            title = cursor.getString(titleCol),
                            artist = cursor.getString(artistCol),
                            albumId = albumId,
                            songUri = "",
                            albumName = cursor.getString(albumNameCol),
                            coverUri = buildCoverUri(albumId)
                        ),
                    )
                }
            }
            songs
        }

    }

    private fun buildCoverUri(albumId: Long): String {
        return "content://media/external/audio/media/$albumId/albumart"
    }
}