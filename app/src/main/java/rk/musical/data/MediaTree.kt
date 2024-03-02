package rk.musical.data

import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import rk.musical.data.model.toMediaItem
import rk.musical.data.model.toMediaItems

const val ROOT = "/"
const val ALBUMS_NODE = "albums"
const val SONGS_NODE = "songs"
private const val SONGS_NODE_TITLE = "Songs"
private const val ALBUMS_NODE_TITLE = "Albums"

class MediaTree(
    private val songsDataSource: LocalSongsDataSource,
    private val albumDataSource: AlbumDataSource
) {
    private val mediaIdToChildren = mutableMapOf<String, MutableList<MediaItem>>()
    private val mediaIdToMediaItem = mutableMapOf<String, MediaItem>()

    private val albumsNodeMediaItem by lazy {
        MediaItem.Builder()
            .setMediaId(ALBUMS_NODE)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(ALBUMS_NODE_TITLE)
                    .setIsPlayable(false)
                    .setIsBrowsable(true)
                    .build()
            )
            .build()
    }
    private val songsNodeMediaItem by lazy {
        MediaItem.Builder()
            .setMediaId(SONGS_NODE)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(SONGS_NODE_TITLE)
                    .setIsPlayable(false)
                    .setIsBrowsable(true)
                    .build()
            )
            .build()
    }

    init {
        initializeTree()
    }

    private fun initializeTree() {
        val rootList: MutableList<MediaItem> = mutableListOf()
        rootList.add(albumsNodeMediaItem)
        rootList.add(songsNodeMediaItem)
        mediaIdToChildren[ROOT] = rootList

        val albumsRoot = mutableListOf<MediaItem>()
        albumDataSource.forEach { album ->
            albumsRoot.add(album.toMediaItem())
        }
        val songsRoot = mutableListOf<MediaItem>()
        mediaIdToChildren[SONGS_NODE] = songsRoot
        mediaIdToChildren[ALBUMS_NODE] = albumsRoot
        albumsRoot.forEach {
            val albumChildren = songsDataSource.getAlbumSongs(it.mediaId).toMediaItems()
            val albumFolder = mutableListOf<MediaItem>()
            albumFolder.addAll(albumChildren)
            mediaIdToChildren[it.mediaMetadata.title.toString()] = albumFolder
        }
    }

    operator fun get(mediaId: String) = mediaIdToChildren[mediaId]

    fun getMediaItemById(mediaId: String) = mediaIdToMediaItem[mediaId]
}
