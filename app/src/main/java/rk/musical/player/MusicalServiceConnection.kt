package rk.musical.player

import android.content.ComponentName
import android.content.Context
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.Player.EVENT_MEDIA_ITEM_TRANSITION
import androidx.media3.common.Player.EVENT_MEDIA_METADATA_CHANGED
import androidx.media3.common.Player.EVENT_PLAYBACK_STATE_CHANGED
import androidx.media3.common.Player.EVENT_PLAY_WHEN_READY_CHANGED
import androidx.media3.session.MediaBrowser
import androidx.media3.session.SessionToken
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.guava.await
import rk.musical.data.ALBUMS_NODE
import rk.musical.data.SONGS_NODE
import rk.musical.data.model.Song
import rk.musical.data.model.logger
import rk.musical.data.model.toMediaItem
import rk.musical.utils.SONG_DURATION
import javax.inject.Inject

@ActivityRetainedScoped
class MusicalServiceConnection @Inject constructor() :
    Player.Listener, MediaBrowser.Listener {

    // initialized when browserFuture connected to the MusicalPlaybackService
    private lateinit var mediaBrowser: MediaBrowser

    val currentPosition: Long
        get() =
            if (this::mediaBrowser.isInitialized)
                mediaBrowser.currentPosition
            else 0L
    val _musicalPlaybackState = MutableStateFlow(MusicalPlaybackState())
    val musicalPlaybackState = _musicalPlaybackState.asStateFlow()


    fun connectToService(context: Context) {
        val sessionToken =
            SessionToken(
                context,
                ComponentName(context, MusicalPlaybackService::class.java)
            )
        val browserFuture = MediaBrowser.Builder(context, sessionToken)
            .setListener(this)
            .buildAsync()
        browserFuture.addListener({
            mediaBrowser = browserFuture.get()
            mediaBrowser.addListener(this)
            syncWithPlaybackService()
            logger("connection initialized")
        }, ContextCompat.getMainExecutor(context))
    }

    fun releaseConnection() {
        mediaBrowser.removeListener(this)
        mediaBrowser.release()
    }

    suspend fun getSongsMediaItems(): List<MediaItem>? {
        return mediaBrowser.getChildren(
            SONGS_NODE, 0, Int.MAX_VALUE, null
        ).await().value
    }

    suspend fun getAlbumsMediaItems(): List<MediaItem>? {
        return mediaBrowser.getChildren(ALBUMS_NODE, 0, Int.MAX_VALUE, null)
            .await().value
    }

    suspend fun getAlbumChild(albumId: String): List<MediaItem>? {
        return mediaBrowser.getChildren(albumId, 0, Int.MAX_VALUE, null)
            .await().value
    }

    private fun syncWithPlaybackService() {
        _musicalPlaybackState.update {
            it.copy(
                isReady = mediaBrowser.playbackState == Player.STATE_IDLE,
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
                    playWhenReady = player.playWhenReady,
                )
            }
        }
    }

    override fun onPlaybackStateChanged(playbackState: Int) {
        _musicalPlaybackState.update {
            it.copy(isReady = playbackState == Player.STATE_READY)
        }
    }


}
