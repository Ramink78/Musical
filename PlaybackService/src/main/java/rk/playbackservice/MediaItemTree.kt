package rk.playbackservice

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata

interface MediaItemTree {
    companion object {
        const val ROOT = "/"
        const val ALBUM_FOLDER = "${ROOT}Album"
        const val RECENT_FOLDER = "${ROOT}Recent"
        const val FAVORITE_FOLDER = "${ROOT}Favorite"
        const val ALBUM_FOLDER_TITLE = "Album"
        const val FAVORITE_FOLDER_TITLE = "Favorite"
        const val RECENT_FOLDER_TITLE = "Recent"
        val rootMediaItem = MediaItem.Builder()
            .setMediaId(ROOT)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setIsPlayable(false)
                    .setIsBrowsable(true)
                    .build()
            )
            .build()
        val albumFolder = MediaItem.Builder()
            .setMediaId(ALBUM_FOLDER)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(ALBUM_FOLDER_TITLE)
                    .setIsPlayable(false)
                    .setIsBrowsable(true)
                    .build()
            )
            .build()
        val recentFolder = MediaItem.Builder()
            .setMediaId(RECENT_FOLDER)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(RECENT_FOLDER_TITLE)
                    .setIsPlayable(false)
                    .setIsBrowsable(true)
                    .build()
            )
            .build()
        val favoriteFolder = MediaItem.Builder()
            .setMediaId(FAVORITE_FOLDER)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(FAVORITE_FOLDER_TITLE)
                    .setIsPlayable(false)
                    .setIsBrowsable(true)
                    .build()
            )
            .build()
    }

    fun getRootMediaItem(): MediaItem
    suspend fun getChildren(parentId: String): List<MediaItem>
    suspend fun getMediaItem(mediaId: String): MediaItem?

}