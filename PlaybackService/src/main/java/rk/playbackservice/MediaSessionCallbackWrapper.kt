package rk.playbackservice

import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.session.LibraryResult
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaLibraryService.MediaLibrarySession
import androidx.media3.session.MediaSession
import com.google.common.collect.ImmutableList
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.guava.future

abstract class MediaSessionCallbackWrapper(
    private val serviceScope: CoroutineScope,
) : MediaLibrarySession.Callback {
    override fun onGetLibraryRoot(
        session: MediaLibrarySession,
        browser: MediaSession.ControllerInfo,
        params: MediaLibraryService.LibraryParams?
    ): ListenableFuture<LibraryResult<MediaItem>> {
        return Futures.immediateFuture(onGetLibraryRootWrapper(session, browser, params))
    }

    override fun onGetChildren(
        session: MediaLibrarySession,
        browser: MediaSession.ControllerInfo,
        parentId: String,
        page: Int,
        pageSize: Int,
        params: MediaLibraryService.LibraryParams?
    ): ListenableFuture<LibraryResult<ImmutableList<MediaItem>>> {
        return serviceScope.future {
            try {
                onGetChildrenWrapper(session, browser, parentId, page, pageSize, params)
            } catch (e: Exception) {
                Log.e(this::class.simpleName, e.toString())
                LibraryResult.ofError(LibraryResult.RESULT_ERROR_BAD_VALUE)
            }

        }
    }

    override fun onGetItem(
        session: MediaLibrarySession,
        browser: MediaSession.ControllerInfo,
        mediaId: String
    ): ListenableFuture<LibraryResult<MediaItem>> {
        return serviceScope.future {
            try {
                onGetItemWrapper(session, browser, mediaId)
            } catch (e: Exception) {
                Log.e(this::class.simpleName, e.toString())
                LibraryResult.ofError(LibraryResult.RESULT_ERROR_BAD_VALUE)
            }
        }
    }

    override fun onGetSearchResult(
        session: MediaLibrarySession,
        browser: MediaSession.ControllerInfo,
        query: String,
        page: Int,
        pageSize: Int,
        params: MediaLibraryService.LibraryParams?
    ): ListenableFuture<LibraryResult<ImmutableList<MediaItem>>> {
        return serviceScope.future {
            try {
                onGetSearchResultWrapper(session, browser, query, page, pageSize, params)
            } catch (e: Exception) {
                Log.e(this::class.simpleName, e.toString())
                LibraryResult.ofError(LibraryResult.RESULT_ERROR_BAD_VALUE)
            }
        }
    }

    override fun onAddMediaItems(
        mediaSession: MediaSession,
        controller: MediaSession.ControllerInfo,
        mediaItems: MutableList<MediaItem>
    ): ListenableFuture<MutableList<MediaItem>> {
        return serviceScope.future {
            try {
                onAddMediaItemsWrapper(mediaSession, controller, mediaItems)
            } catch (e: Exception) {
                Log.e(this::class.simpleName, e.toString())
                mutableListOf()
            }
        }
    }

    protected abstract suspend fun onGetChildrenWrapper(
        session: MediaLibrarySession,
        browser: MediaSession.ControllerInfo,
        parentId: String,
        page: Int,
        pageSize: Int,
        params: MediaLibraryService.LibraryParams?
    ): LibraryResult<ImmutableList<MediaItem>>

    protected abstract fun onGetLibraryRootWrapper(
        session: MediaLibrarySession,
        browser: MediaSession.ControllerInfo,
        params: MediaLibraryService.LibraryParams?
    ): LibraryResult<MediaItem>

    protected abstract suspend fun onAddMediaItemsWrapper(
        mediaSession: MediaSession,
        controller: MediaSession.ControllerInfo,
        mediaItems: MutableList<MediaItem>
    ): MutableList<MediaItem>

    protected abstract suspend fun onGetSearchResultWrapper(
        session: MediaLibrarySession,
        browser: MediaSession.ControllerInfo,
        query: String,
        page: Int,
        pageSize: Int,
        params: MediaLibraryService.LibraryParams?
    ): LibraryResult<ImmutableList<MediaItem>>

    protected abstract suspend fun onGetItemWrapper(
        session: MediaLibrarySession,
        browser: MediaSession.ControllerInfo,
        mediaId: String
    ): LibraryResult<MediaItem>
}