package rk.musical.player

import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import rk.musical.data.model.Song
import rk.musical.data.model.toMediaItem

data class MusicalPlaybackState(
    val isConnected: Boolean = false,
    val playbackState: Int = Player.STATE_IDLE,
    val playWhenReady: Boolean = false,
    val playingMediaItem: MediaItem = Song.Empty.toMediaItem(),
    val isReady: Boolean = false
) {
    val isPlaying: Boolean
        get() {
            return (playbackState == Player.STATE_BUFFERING
                    || playbackState == Player.STATE_READY)
                    && playWhenReady
        }
}
