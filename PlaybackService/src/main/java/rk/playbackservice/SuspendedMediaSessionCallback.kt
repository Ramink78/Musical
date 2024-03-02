package rk.playbackservice

import androidx.media3.common.MediaItem
import androidx.media3.session.LibraryResult
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession
import com.google.common.collect.ImmutableList
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

class SuspendedMediaSessionCallback @Inject constructor(
    private val mediaItemTree: MediaItemTree,
    serviceScoped: CoroutineScope
) :
    MediaSessionCallbackWrapper(serviceScoped) {

    override suspend fun onGetChildrenWrapper(
        session: MediaLibraryService.MediaLibrarySession,
        browser: MediaSession.ControllerInfo,
        parentId: String,
        page: Int,
        pageSize: Int,
        params: MediaLibraryService.LibraryParams?
    ): LibraryResult<ImmutableList<MediaItem>> {
        return LibraryResult.ofItemList(mediaItemTree.getChildren(parentId), params)
    }

    override fun onGetLibraryRootWrapper(
        session: MediaLibraryService.MediaLibrarySession,
        browser: MediaSession.ControllerInfo,
        params: MediaLibraryService.LibraryParams?
    ): LibraryResult<MediaItem> {
        return LibraryResult.ofItem(mediaItemTree.getRootMediaItem(), params)
    }

    override suspend fun onAddMediaItemsWrapper(
        mediaSession: MediaSession,
        controller: MediaSession.ControllerInfo,
        mediaItems: MutableList<MediaItem>
    ): MutableList<MediaItem> {
        return mediaItems.map {
            mediaItemTree.getMediaItem(it.mediaId)!!
        }.toMutableList()
    }

    override suspend fun onGetSearchResultWrapper(
        session: MediaLibraryService.MediaLibrarySession,
        browser: MediaSession.ControllerInfo,
        query: String,
        page: Int,
        pageSize: Int,
        params: MediaLibraryService.LibraryParams?
    ): LibraryResult<ImmutableList<MediaItem>> {
        TODO("Not yet implemented")
    }

    override suspend fun onGetItemWrapper(
        session: MediaLibraryService.MediaLibrarySession,
        browser: MediaSession.ControllerInfo,
        mediaId: String
    ): LibraryResult<MediaItem> {
        TODO("Not yet implemented")

    }

}