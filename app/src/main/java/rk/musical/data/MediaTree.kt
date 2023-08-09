package rk.musical.data

import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import rk.musical.data.model.toMediaItem

const val ROOT = "/"
const val ALBUMS_NODE = "albums"
const val SONGS_NODE = "songs"
private const val SONGS_NODE_TITLE = "Songs"
private const val ALBUMS_NODE_TITLE = "Albums"

class MediaTree(
    private val songsDataSource: LocalSongsDataSource,
    private val albumDataSource: AlbumDataSource
) {
    private val tree = mutableMapOf<String, MediaNode>()

    data class MediaNode(
        val mediaItem: MediaItem,
        val children: List<MediaItem>? = null
    )

    init {
        initializeTree()
    }

    private fun initializeTree() {
        val albumsMediaItem = MediaItem.Builder()
            .setMediaId(ALBUMS_NODE)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(ALBUMS_NODE_TITLE)
                    .setIsPlayable(false)
                    .setIsBrowsable(true)
                    .build()
            )
            .build()
        val songsMediaItem = MediaItem.Builder()
            .setMediaId(SONGS_NODE)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(SONGS_NODE_TITLE)
                    .setIsPlayable(false)
                    .setIsBrowsable(true)
                    .build()
            )
            .build()
        initializeRoot(listOf(albumsMediaItem, songsMediaItem))
        tree[ALBUMS_NODE] = buildAlbumsNode(albumsMediaItem)
        tree[SONGS_NODE] = buildSongsNode(songsMediaItem)

    }

    private fun initializeRoot(children: List<MediaItem>) {
        val rootMediaItem = MediaItem.Builder()
            .setMediaId(ROOT)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setIsBrowsable(true)
                    .setIsPlayable(false)
                    .build()
            )
            .build()
        tree[rootMediaItem.mediaId] = MediaNode(
            mediaItem = rootMediaItem,
            children = children
        )

    }

    private fun buildAlbumsNode(albumsMediaItem: MediaItem) =
        MediaNode(
            mediaItem = albumsMediaItem,
            children = albumDataSource.map { it.toMediaItem() }
        )

    private fun buildSongsNode(songsMediaItem: MediaItem) =
        MediaNode(
            mediaItem = songsMediaItem,
            children = songsDataSource.map { it.toMediaItem() }
        )


    fun getChildren(parentId: String): List<MediaItem>? =
        tree[parentId]?.children


    fun getRoot() = tree[ROOT]!!.mediaItem


}