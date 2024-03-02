package rk.musical.data.model

import android.content.ContentUris
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.room.Entity
import androidx.room.PrimaryKey
import rk.musical.utils.ALBUM_ID
import rk.musical.utils.SONGS_URI
import rk.musical.utils.SONG_DURATION
import rk.musical.utils.buildSongMediaItem

@Entity(tableName = "favorite")
data class Song(
    @PrimaryKey val id: String,
    val title: String,
    val artist: String,
    val songUri: String,
    val albumName: String,
    val duration: Long,
    val coverUri: String? = null,
    val albumId: String
) {
    companion object {
        val Empty =
            Song(
                id = "",
                title = "",
                artist = "",
                songUri = "",
                albumName = "",
                duration = 0,
                albumId = ""
            )
    }
}

fun Song.toMediaItem() =
    buildSongMediaItem(
        songId = id,
        title = title,
        artist = artist,
        songUri = songUri.toUri(),
        albumName = albumName,
        coverUri = coverUri?.toUri(),
        duration = duration,
        albumId = albumId
    )

fun MediaItem.toSong() =
    Song(
        id = mediaId,
        title = mediaMetadata.title.toString(),
        artist = mediaMetadata.artist.toString(),
        songUri = ContentUris.withAppendedId(SONGS_URI, mediaId.toLongOrNull() ?: 0L).toString(),
        albumName = mediaMetadata.albumTitle.toString(),
        coverUri = mediaMetadata.artworkUri.toString(),
        duration = mediaMetadata.extras?.getLong(SONG_DURATION, 0L) ?: 0L,
        albumId = mediaMetadata.extras?.getString(ALBUM_ID, "") ?: ""
    )

fun List<MediaItem>.toSongs() = map { it.toSong() }

fun List<Song>.toMediaItems() = map { it.toMediaItem() }
