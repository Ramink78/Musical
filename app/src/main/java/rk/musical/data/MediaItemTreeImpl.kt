package rk.musical.data

import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import rk.musical.data.model.toMediaItem
import rk.musical.data.model.toMediaItems
import rk.playbackservice.MediaItemTree

class MediaItemTreeImpl(
    private val albumRepository: AlbumRepository,
    private val songRepository: SongRepository,
    private val favoriteRepository: FavoriteRepository
) : MediaItemTree {
    private val idToChildren = mutableMapOf<String, MutableList<MediaItem>>()
    private val idToMediaItem = mutableMapOf<String, MediaItem>()
    private val rootId = "/"
    private val albumCategoryId = "Album"
    private val recentCategoryId = "Recent"
    private val favoriteCategoryId = "Favorite"
    private val rootMediaItem = MediaItem.Builder().setMediaId(rootId).setMediaMetadata(
        MediaMetadata.Builder().setIsPlayable(false).setIsBrowsable(true).build()
    ).build()
    private val albumCategory = MediaItem.Builder().setMediaId(albumCategoryId).setMediaMetadata(
        MediaMetadata.Builder().setTitle(albumCategoryId).setIsPlayable(false).setIsBrowsable(true)
            .build()
    ).build()
    private val recentCategory = MediaItem.Builder().setMediaId(recentCategoryId).setMediaMetadata(
        MediaMetadata.Builder().setTitle(recentCategoryId).setIsPlayable(false).setIsBrowsable(true)
            .build()
    ).build()
    private val favoriteCategory =
        MediaItem.Builder().setMediaId(favoriteCategoryId).setMediaMetadata(
            MediaMetadata.Builder().setTitle(favoriteCategoryId).setIsPlayable(false)
                .setIsBrowsable(true).build()
        ).build()

    init {
        initRoot()
    }

    private fun initRoot() {
        val rootList = mutableListOf<MediaItem>()
        rootList += albumCategory
        rootList += favoriteCategory
        rootList += recentCategory
        idToChildren[rootId] = rootList
    }

    private suspend fun initAlbumCategory() {
        val albumList = mutableListOf<MediaItem>()
        albumRepository.load()
        songRepository.load()
        albumList.addAll(albumRepository.cachedAlbums.toMediaItems())
        albumRepository.cachedAlbums.forEach {
            idToMediaItem[it.id] = it.toMediaItem()
        }
        songRepository.chacedSongs.forEach {
            idToMediaItem[it.id] = it.toMediaItem()
        }
        idToChildren[albumCategoryId] = albumList
        val songsByAlbumId = songRepository.chacedSongs.groupBy { it.albumId }.mapValues {
            it.value.toMediaItems().toMutableList()
        }
        idToChildren.putAll(songsByAlbumId)
    }

    private suspend fun initFavoriteCategory() {
        val favoriteList = mutableListOf<MediaItem>()
        favoriteList.addAll(favoriteRepository.getAllFavorites().toMediaItems())
        idToChildren[favoriteCategoryId] = favoriteList
    }

    override fun getRootMediaItem(): MediaItem {
        return rootMediaItem
    }

    override suspend fun getChildren(parentId: String): List<MediaItem> {
        initAlbumCategory()
        initFavoriteCategory()
        return idToChildren[parentId]?.toList() ?: emptyList()
    }

    override suspend fun getMediaItem(mediaId: String): MediaItem? {
        return idToMediaItem[mediaId]

    }
}
