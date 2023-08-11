package rk.musical.player

import android.content.ComponentName
import android.content.Context
import androidx.compose.runtime.Stable
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.Player.EVENT_MEDIA_ITEM_TRANSITION
import androidx.media3.common.Player.EVENT_MEDIA_METADATA_CHANGED
import androidx.media3.common.Player.EVENT_PLAYBACK_STATE_CHANGED
import androidx.media3.common.Player.EVENT_PLAY_WHEN_READY_CHANGED
import androidx.media3.session.MediaBrowser
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.guava.await
import kotlinx.coroutines.launch
import rk.musical.data.ALBUMS_NODE
import rk.musical.data.SONGS_NODE
import rk.musical.data.model.Song
import rk.musical.data.model.logger
import rk.musical.data.model.toMediaItem
import rk.musical.utils.SONG_DURATION

@Stable
class MusicalServiceConnection private constructor(
    context: Context, component: ComponentName
) : Player.Listener, MediaBrowser.Listener {
    private val scope = CoroutineScope(Dispatchers.Main)
    private val sessionToken by lazy {
        SessionToken(context, component)
    }
    private val browserFuture by lazy {
        MediaBrowser.Builder(context, sessionToken)
            .setListener(this)
            .buildAsync()
    }

    // initialized when browserFuture connected to the MusicalPlaybackService
    private lateinit var mediaBrowser: MediaBrowser

    private val _musicalPlaybackState = MutableStateFlow(MusicalPlaybackState())
    val musicalPlaybackState = _musicalPlaybackState.asStateFlow()

    val currentPosition: Long
        get() = mediaBrowser.currentPosition

    init {
        browserFuture.addListener({
            mediaBrowser = browserFuture.get()
            mediaBrowser.addListener(this)
            syncWithPlaybackService()
            logger("connection initialized")
        }, ContextCompat.getMainExecutor(context))

    }

    private fun releaseConnection() {
        mediaBrowser.removeListener(this)
        mediaBrowser.release()
        instance = null
    }

    suspend fun getSongsMediaItems(): List<MediaItem>? {
        return mediaBrowser.getChildren(
            SONGS_NODE, 0, Int.MAX_VALUE, null
        ).await().value?.toList()
    }

    suspend fun getAlbumsMediaItems(): List<MediaItem>? {
        return mediaBrowser.getChildren(ALBUMS_NODE, 0, Int.MAX_VALUE, null)
            .await().value?.toList()
    }

    private fun syncWithPlaybackService() {
        _musicalPlaybackState.update {
            it.copy(
                isConnected = mediaBrowser.isConnected,
                playingMediaItem = mediaBrowser.currentMediaItem ?: MediaItem.EMPTY,
                playWhenReady = mediaBrowser.playWhenReady,
                playbackState = mediaBrowser.playbackState,
            )
        }

    }



    fun playSong(song: Song) {
        mediaBrowser.setMediaItem(song.toMediaItem())
        mediaBrowser.prepare()
        mediaBrowser.play()
    }

    fun resume() {
        mediaBrowser.play()
    }

    fun pause() {
        mediaBrowser.pause()
    }

    fun seekTo(progress: Float) {
        mediaBrowser.seekTo(
            (progress * musicalPlaybackState.value.playingMediaItem.mediaMetadata.extras!!.getLong(
                SONG_DURATION, 0
            )
                    ).toLong()

        )

    }


    private fun updatePlayingMediaItem(mediaItem: MediaItem?) {
        if (mediaItem == null) return
        _musicalPlaybackState.update {
            it.copy(playingMediaItem = mediaItem)
        }
    }

    override fun onEvents(player: Player, events: Player.Events) {
        if (events.contains(EVENT_MEDIA_METADATA_CHANGED)
            || events.contains(EVENT_MEDIA_ITEM_TRANSITION)
            || events.contains(EVENT_PLAY_WHEN_READY_CHANGED)
        ) {
            updatePlayingMediaItem(player.currentMediaItem)
        }
        if (events.contains(EVENT_PLAY_WHEN_READY_CHANGED)
            || events.contains(EVENT_PLAYBACK_STATE_CHANGED)
            || events.contains(EVENT_MEDIA_ITEM_TRANSITION)
        ) {
            _musicalPlaybackState.update {
                it.copy(
                    playbackState = player.playbackState,
                    playWhenReady = player.playWhenReady
                )

            }
        }
    }

    override fun onDisconnected(controller: MediaController) {
        releaseConnection()
        _musicalPlaybackState.update {
            it.copy(isConnected = false)
        }
    }

    companion object {
        @Volatile
        private var instance: MusicalServiceConnection? = null
        fun getInstance(context: Context, component: ComponentName): MusicalServiceConnection =
            instance ?: synchronized(this) {
                instance ?: MusicalServiceConnection(context, component)
                    .also { instance = it }
            }

    }
}
