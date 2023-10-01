package rk.musical.player

import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import rk.musical.data.model.Song
import rk.musical.data.model.toSong

fun Player.isPlayingFlow() = callbackFlow {
    send(isPlaying)
    val listener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            trySendBlocking(isPlaying)
        }
    }
    addListener(listener)
    awaitClose { removeListener(listener) }
}.flowOn(Dispatchers.Main)

fun Player.playingSongFlow() = callbackFlow {
    send(currentMediaItem?.toSong() ?: Song.Empty)
    val listener = object : Player.Listener {
        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            if (mediaItem == null) return
            trySendBlocking(mediaItem.toSong())
        }
    }
    addListener(listener)
    awaitClose { removeListener(listener) }
}.flowOn(Dispatchers.Main)

fun Player.currentPositionFlow() = flow {
    while (true) {
        emit(currentPosition)
        delay(500)
    }
}.flowOn(Dispatchers.Main)