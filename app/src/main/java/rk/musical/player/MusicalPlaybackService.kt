package rk.musical.player

import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.LibraryResult
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession
import com.google.common.collect.ImmutableList
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.guava.future
import rk.musical.data.AlbumRepository
import rk.musical.data.MediaTree
import rk.musical.data.ROOT
import rk.musical.data.SongRepository
import javax.inject.Inject

@UnstableApi
@AndroidEntryPoint
class MusicalPlaybackService : MediaLibraryService() {
    @Inject
    lateinit var exoPlayer: ExoPlayer

    @Inject
    lateinit var albumRepository: AlbumRepository

    @Inject
    lateinit var songRepository: SongRepository


    private val mediaTree: MediaTree by lazy {
        MediaTree(songRepository, albumRepository)
    }
    private val notificationProvider by lazy {
        MusicalNotificationProvider(this)
    }

    private var mediaSession: MediaLibrarySession? = null
    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)
    private val isLoadedRepositories
        get() =
            songRepository.isReady && albumRepository.isReady


    // onGetLibraryRoot immediate return this
    private val rootMediaItem by lazy {
        MediaItem.Builder()
            .setMediaId(ROOT)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setIsBrowsable(true)
                    .setIsPlayable(false)
                    .build()
            )
            .build()
    }


    override fun onCreate() {
        super.onCreate()
        mediaSession = buildMediaSession(exoPlayer)
    }

    private fun buildMediaSession(exoPlayer: ExoPlayer) =
        with(MediaLibrarySession.Builder(this, exoPlayer, LibrarySessionCallback())) {
            setId(packageName)
            setMediaNotificationProvider(notificationProvider)
            packageManager?.getLaunchIntentForPackage(packageName)?.let {
                setSessionActivity(
                    PendingIntent.getActivity(
                        this@MusicalPlaybackService,
                        0,
                        it,
                        FLAG_IMMUTABLE, null
                    )
                )
            }
            build()
        }

    private fun <T> whenReadyTree(action: () -> T): ListenableFuture<T> {
        return if (isLoadedRepositories) {
            Futures.immediateFuture(action())
        } else {
            return serviceScope.future {
                // wait until all repositories ready
                songRepository.load()
                albumRepository.load()
                action()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        destroySession()
    }


    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo) = mediaSession
    private fun destroySession() {
        mediaSession?.run {
            release()
            player.release()
            mediaSession = null
        }
        serviceJob.cancel()
    }


    private inner class LibrarySessionCallback : MediaLibrarySession.Callback {
        override fun onSubscribe(
            session: MediaLibrarySession,
            browser: MediaSession.ControllerInfo,
            parentId: String,
            params: LibraryParams?
        ): ListenableFuture<LibraryResult<Void>> {
            val children =
                mediaTree[parentId]
                    ?: return Futures.immediateFuture(
                        LibraryResult.ofError(LibraryResult.RESULT_ERROR_BAD_VALUE)
                    )
            session.notifyChildrenChanged(browser, parentId, children.size, params)
            return Futures.immediateFuture(LibraryResult.ofVoid())
        }

        override fun onGetLibraryRoot(
            session: MediaLibrarySession,
            browser: MediaSession.ControllerInfo,
            params: LibraryParams?
        ): ListenableFuture<LibraryResult<MediaItem>> {
            return Futures.immediateFuture(LibraryResult.ofItem(rootMediaItem, null))
        }

        override fun onGetChildren(
            session: MediaLibrarySession,
            browser: MediaSession.ControllerInfo,
            parentId: String,
            page: Int,
            pageSize: Int,
            params: LibraryParams?
        ): ListenableFuture<LibraryResult<ImmutableList<MediaItem>>> {
            return whenReadyTree {
                val children = mediaTree[parentId]
                LibraryResult.ofItemList(children ?: ImmutableList.of(), null)
            }
        }

        override fun onGetItem(
            session: MediaLibrarySession,
            browser: MediaSession.ControllerInfo,
            mediaId: String
        ): ListenableFuture<LibraryResult<MediaItem>> {
            return whenReadyTree {
                LibraryResult.ofItem(mediaTree.getMediaItemById(mediaId) ?: MediaItem.EMPTY, null)
            }
        }

    }


}