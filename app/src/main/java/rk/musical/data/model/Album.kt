package rk.musical.data.model

import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import rk.musical.utils.buildAlbumMediaItem

data class Album(
    val id: String,
    val title: String,
    val artist: String,
    val songsCount: Int,
    val coverUri: String? = null
)

fun Album.toMediaItem() =
    buildAlbumMediaItem(
        title = title,
        artist = artist,
        coverUri = coverUri?.toUri(),
        songsCount = songsCount,
        id = id
    )

fun List<Album>.toMediaItems() = map { it.toMediaItem() }

fun List<MediaItem>.toAlbums() = map { it.toAlbum() }

fun MediaItem.toAlbum() =
    Album(
        id = mediaId,
        title = mediaMetadata.title.toString(),
        artist = mediaMetadata.artist.toString(),
        songsCount = mediaMetadata.totalTrackCount ?: 0,
        coverUri = mediaMetadata.artworkUri.toString()
    )
