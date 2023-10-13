package rk.musical.player

import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import rk.musical.data.model.Song
import rk.musical.data.model.toSong

fun Player.isPlayingFlow() =
    callbackFlow {
        send(isPlaying)
        val listener =
            object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    trySendBlocking(isPlaying)
                }
            }
        addListener(listener)
        awaitClose { removeListener(listener) }
    }.flowOn(Dispatchers.Main)

fun Player.playingSongFlow() =
    callbackFlow {
        send(currentMediaItem?.toSong() ?: Song.Empty)
        val listener =
            object : Player.Listener {
                override fun onMediaItemTransition(
                    mediaItem: MediaItem?,
                    reason: Int,
                ) {
                    if (mediaItem == null) return
                    trySendBlocking(mediaItem.toSong())
                }
            }
        addListener(listener)
        awaitClose { removeListener(listener) }
    }.flowOn(Dispatchers.Main)

// this flow emit only in NowPlaying expanded state
fun Player.currentPositionFlow() =
    flow {
        while (true) {
            emit(currentPosition)
        }
    }.flowOn(Dispatchers.Main)

fun Player.repeatModeFlow() =
    callbackFlow {
        send(repeatMode)
        val listener =
            object : Player.Listener {
                override fun onRepeatModeChanged(repeatMode: Int) {
                    trySendBlocking(repeatMode)
                }
            }
        addListener(listener)
        awaitClose { removeListener(listener) }
    }.flowOn(Dispatchers.Main)

fun Player.shuffleModeFlow() =
    callbackFlow {
        send(shuffleModeEnabled)
        val listener =
            object : Player.Listener {
                override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
                    trySendBlocking(shuffleModeEnabled)
                }
            }
        addListener(listener)
        awaitClose { removeListener(listener) }
    }.flowOn(Dispatchers.Main)
