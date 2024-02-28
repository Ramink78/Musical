package rk.playbackservice

import androidx.media3.common.MediaItem

interface MediaItemTree {
    fun getRoot(): MediaItem
    fun getChildren(parentId: String): List<MediaItem>
    fun getMediaItem(mediaId: String): MediaItem?

}