package rk.musical.utils

import android.net.Uri
import androidx.core.os.bundleOf
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata

fun buildSongMediaItem(
    songUri: Uri,
    songId: String,
    title: String,
    artist: String,
    albumName: String,
    coverUri: Uri?,
    duration: Long,
    albumId: String
) = MediaItem.Builder()
    .setMediaId(songId)
    .setUri(songUri)
    .setMediaMetadata(
        MediaMetadata.Builder()
            .setIsBrowsable(false)
            .setIsPlayable(true)
            .setArtworkUri(coverUri)
            .setMediaType(MediaMetadata.MEDIA_TYPE_MUSIC)
            .setTitle(title)
            .setArtist(artist)
            .setAlbumTitle(albumName)
            .setExtras(
                bundleOf(
                    SONG_DURATION to duration,
                    ALBUM_ID to albumId
                )
            )
            .build()
    )
    .build()

fun buildAlbumMediaItem(
    title: String,
    id: String,
    artist: String,
    songsCount: Int,
    coverUri: Uri?
) = MediaItem.Builder()
    .setMediaId(id)
    .setMediaMetadata(
        MediaMetadata.Builder()
            .setIsBrowsable(true)
            .setIsPlayable(false)
            .setTitle(title)
            .setArtist(artist)
            .setMediaType(MediaMetadata.MEDIA_TYPE_ALBUM)
            .setTotalTrackCount(songsCount)
            .setArtworkUri(coverUri)
            .build()
    )
    .build()
