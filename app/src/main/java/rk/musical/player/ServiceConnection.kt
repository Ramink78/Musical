package rk.musical.player

import android.content.ComponentName
import android.content.Context
import androidx.core.content.ContextCompat
import androidx.media3.session.MediaBrowser
import androidx.media3.session.SessionToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class ServiceConnection {
    private val _state = MutableSharedFlow<ConnectionEvent>()
    val state: SharedFlow<ConnectionEvent> = _state.asSharedFlow()
    private lateinit var mediaBrowser: MediaBrowser
    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.Main + job)

    fun connect(context: Context) {
        val sessionToken =
            SessionToken(
                context,
                ComponentName(context, MusicalPlaybackService::class.java)
            )
        val future = MediaBrowser.Builder(context, sessionToken).buildAsync()
        future.addListener({
            mediaBrowser = future.get()
            scope.launch { _state.emit(ConnectionEvent.Connected(mediaBrowser)) }
        }, ContextCompat.getMainExecutor(context))
    }

    fun disconnect() {
        mediaBrowser.release()
        scope.launch { _state.emit(ConnectionEvent.Disconnected) }
    }
}

sealed class ConnectionEvent {
    object Disconnected : ConnectionEvent()
    data class Connected(val mediaBrowser: MediaBrowser) : ConnectionEvent()
}