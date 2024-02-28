package rk.musical.data

import androidx.media3.common.MediaItem
import rk.musical.data.model.toMediaItem
import rk.musical.data.model.toMediaItems
import rk.playbackservice.MediaItemTree

class MediaItemTreeImpl(
    private val albumRepository: AlbumRepository,
    private val songRepository: SongRepository
) : MediaItemTree {
    override fun getRootMediaItem(): MediaItem {
        return MediaItemTree.rootMediaItem
    }

    override suspend fun getChildren(parentId: String): List<MediaItem> {
        return when (parentId) {
            MediaItemTree.ROOT ->
                listOf(
                    MediaItemTree.albumFolder,
                    MediaItemTree.recentFolder,
                    MediaItemTree.favoriteFolder
                )

            MediaItemTree.ALBUM_FOLDER -> {
                albumRepository.loadAlbums()
                albumRepository.cachedAlbums.toMediaItems()
            }

            MediaItemTree.FAVORITE_FOLDER -> {
                // implement favorite folder
                emptyList()
            }

            MediaItemTree.RECENT_FOLDER -> {
                // implement recent folder
                emptyList()
            }

            else -> {
                // select an album child
                songRepository.load()
                // parentId is album name
                songRepository.getAlbumSongs(parentId).toMediaItems()
            }
        }
    }

    override suspend fun getMediaItem(mediaId: String): MediaItem? {
        songRepository.load()
        val foundedSong = songRepository.chacedSongs.find { it.id == mediaId }
        return foundedSong?.toMediaItem()
    }
}
