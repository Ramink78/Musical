package rk.playbackservice

import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession
import javax.inject.Inject

class MusicalPlaybackService : MediaLibraryService() {
    @Inject
    lateinit var mediaSessionCallback: MediaSessionCallback

    @Inject
    lateinit var exoPlayer: ExoPlayer
    private var mediaLibrarySession: MediaLibrarySession? = null

    override fun onCreate() {
        super.onCreate()
        mediaLibrarySession =
            MediaLibrarySession.Builder(this, exoPlayer, mediaSessionCallback).build()
    }

    override fun onDestroy() {
        mediaLibrarySession?.run {
            player.release()
            release()
            mediaLibrarySession = null
        }
        super.onDestroy()
    }

    // Allow all client to access this session
    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaLibrarySession? {
        return mediaLibrarySession
    }
}