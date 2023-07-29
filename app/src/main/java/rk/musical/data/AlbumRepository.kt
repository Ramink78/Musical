package rk.musical.data

import android.content.ContentUris
import android.content.Context
import android.os.Build
import android.provider.MediaStore
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
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

class AlbumRepository(private val context: Context) {

    suspend fun loadAlbums(dispatcher: CoroutineDispatcher = Dispatchers.IO): List<Album> {
        val albums = mutableListOf<Album>()
        return withContext(dispatcher) {
            context.contentResolver.kuery(
                uri = ALBUMS_URI,
                columns = albumColumns,
            )?.use {
                val albumNameCol = it.albumNameColumnIndex
                val albumIdCol = it.albumIdColumnIndex
                val albumArtCol = it.albumArtColumnIndex
                val artistCol = it.artistColumnIndex
                val albumSongsCountCol = it.albumSongsCountColumnIndex
                while (it.moveToNext()) {
                    val albumId = it.getLong(albumIdCol)
                    val coverUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        ContentUris.withAppendedId(
                            MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                            albumId
                        ).toString()
                    } else {
                        it.getString(albumArtCol)
                    }

                    albums += Album(
                        id = albumId,
                        title = it.getString(albumNameCol),
                        artist = it.getString(artistCol),
                        songsCount = it.getInt(albumSongsCountCol),
                        coverUri = coverUri
                    )
                }
            }
            albums
        }
    }
}