package rk.musical.player

import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaBrowser
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
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
import javax.inject.Inject

@ActivityRetainedScoped
class MusicalRemoteControl @Inject constructor(
    private val serviceConnection: ServiceConnection
) : Player.Listener {
    private lateinit var mediaBrowser: MediaBrowser
    private var remoteJob = SupervisorJob()
    private val remoteScope = CoroutineScope(Dispatchers.Main + remoteJob)
    private val _browserEvent = MutableStateFlow<BrowserEvent>(BrowserEvent.Disconnected)
    val browserEvent = _browserEvent.asStateFlow()
    private val _musicalPlaybackState = MutableStateFlow(MusicalPlaybackState())
    val musicalPlaybackState = _musicalPlaybackState.asStateFlow()
    private val _playbackPosition = MutableStateFlow(0L)
    val playbackPosition = _playbackPosition.asStateFlow()

    init {
        remoteScope.launch {
            serviceConnection.connectionEvent.collect { event ->
                when (event) {
                    is ServiceConnectionEvent.Connected -> {
                        mediaBrowser = event.mediaBrowser
                        mediaBrowser.addListener(this@MusicalRemoteControl)
                        _browserEvent.value = BrowserEvent.Connected
                        logger("Service connected")
                        syncWithPlaybackService(event.mediaBrowser)
                    }

                    ServiceConnectionEvent.Disconnected -> {
                        logger("Service disconnected")
                        mediaBrowser.removeListener(this@MusicalRemoteControl)
                        _browserEvent.value = BrowserEvent.Disconnected
                    }

                }
            }
        }
    }

    private fun syncWithPlaybackService(mediaBrowser: MediaBrowser) {
        _musicalPlaybackState.update {
            it.copy(
                isReady = mediaBrowser.playbackState == Player.STATE_READY,
                isConnected = mediaBrowser.isConnected,
                playingMediaItem = mediaBrowser.currentMediaItem ?: MediaItem.EMPTY,
                playWhenReady = mediaBrowser.playWhenReady,
                playbackState = mediaBrowser.playbackState,
            )
        }
        remoteScope.launch {
            while (true) {
                _playbackPosition.emit(mediaBrowser.currentPosition)
                delay(1000)
            }
        }

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

    fun skipNext() {
        mediaBrowser.seekToNext()
    }

    fun skipPrevious() {
        mediaBrowser.seekToPrevious()
    }

    fun playSongFromIndex(index: Int) {
        mediaBrowser.seekTo(index, 0L)
        mediaBrowser.prepare()
        mediaBrowser.playWhenReady = true
    }

    fun setPlaylist(playlist: List<MediaItem>) {
        mediaBrowser.setMediaItems(playlist, false)
    }

    private fun updatePlayingMediaItem(mediaItem: MediaItem?) {
        if (mediaItem == null) return
        _musicalPlaybackState.update {
            it.copy(playingMediaItem = mediaItem)
        }
    }

    override fun onEvents(player: Player, events: Player.Events) {
        if (events.contains(Player.EVENT_MEDIA_METADATA_CHANGED)
            || events.contains(Player.EVENT_MEDIA_ITEM_TRANSITION)
            || events.contains(Player.EVENT_PLAY_WHEN_READY_CHANGED)
        ) {
            updatePlayingMediaItem(player.currentMediaItem)
        }
        if (events.contains(Player.EVENT_PLAY_WHEN_READY_CHANGED)
            || events.contains(Player.EVENT_PLAYBACK_STATE_CHANGED)
            || events.contains(Player.EVENT_MEDIA_ITEM_TRANSITION)
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

    sealed interface BrowserEvent {
        object Connected : BrowserEvent
        object Disconnected : BrowserEvent
    }

}