package rk.musical.player

import androidx.media3.common.MediaItem
import androidx.media3.common.Player

data class MusicalPlaybackState(
    val isConnected: Boolean = false,
    val playbackState: Int = Player.STATE_IDLE,
    val playWhenReady: Boolean = false,
    val playingMediaItem: MediaItem = MediaItem.EMPTY,
    val currentPosition: Long = 0
) {
    val isPlaying: Boolean
        get() {
            return (playbackState == Player.STATE_BUFFERING
                    || playbackState == Player.STATE_READY)
                    && playWhenReady
        }
}
