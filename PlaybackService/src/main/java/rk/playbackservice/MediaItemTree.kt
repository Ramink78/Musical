package rk.playbackservice

import androidx.media3.common.MediaItem

interface MediaItemTree {

    fun getRootMediaItem(): MediaItem
    suspend fun getChildren(parentId: String): List<MediaItem>
    suspend fun getMediaItem(mediaId: String): MediaItem?

}