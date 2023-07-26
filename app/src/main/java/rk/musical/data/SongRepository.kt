package rk.musical.data

import android.content.ContentUris
import android.content.Context
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import rk.musical.data.model.Song
import rk.musical.utils.IS_MUSIC_CLAUSE
import rk.musical.utils.SONGS_URI
import rk.musical.utils.SortOrder
import rk.musical.utils.albumId
import rk.musical.utils.artist
import rk.musical.utils.id
import rk.musical.utils.kuery
import rk.musical.utils.songColumns
import rk.musical.utils.title

class SongRepository(private val context: Context) {


    suspend fun loadSongs(
        sortOrder: String = SortOrder.Descending.dateAdded,
        dispatcher: CoroutineDispatcher = Dispatchers.IO
    ): List<Song> {
        val songs = mutableListOf<Song>()
        withContext(dispatcher) {
            delay(1500)
            context.contentResolver.kuery(
                uri = SONGS_URI,
                columns = songColumns,
                sortOrder = sortOrder,
                selection = IS_MUSIC_CLAUSE
            )?.use { cursor ->
                while (cursor.moveToNext()) {

                    songs.add(
                        Song(
                            id = cursor.id,
                            title = cursor.title,
                            artist = cursor.artist,
                            albumId = cursor.albumId,
                            songUri = ContentUris.withAppendedId(SONGS_URI, cursor.id.toLong())
                        )
                    )
                }
            }
        }
        return songs
    }


}