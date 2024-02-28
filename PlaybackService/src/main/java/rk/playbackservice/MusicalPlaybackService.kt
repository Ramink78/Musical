package rk.playbackservice

import android.content.Intent
import android.os.IBinder
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ServiceLifecycleDispatcher
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MusicalPlaybackService : MediaLibraryService(), LifecycleOwner {
    private val dispatcher = ServiceLifecycleDispatcher(this)

    @Inject
    lateinit var mediaSessionCallback: SuspendedMediaSessionCallback

    @Inject
    lateinit var exoPlayer: ExoPlayer
    private var mediaLibrarySession: MediaLibrarySession? = null

    override fun onCreate() {
        dispatcher.onServicePreSuperOnCreate()
        super.onCreate()
        mediaLibrarySession =
            MediaLibrarySession.Builder(this, exoPlayer, mediaSessionCallback).build()
    }

    override fun onDestroy() {
        dispatcher.onServicePreSuperOnDestroy()
        mediaLibrarySession?.run {
            player.release()
            release()
            mediaLibrarySession = null
        }
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        dispatcher.onServicePreSuperOnBind()
        return super.onBind(intent)
    }

    // Allow all client to access this session
    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaLibrarySession? {
        return mediaLibrarySession
    }

    override val lifecycle: Lifecycle
        get() = dispatcher.lifecycle


}