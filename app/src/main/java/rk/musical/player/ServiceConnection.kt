package rk.musical.player

import android.content.ComponentName
import android.content.Context
import androidx.core.content.ContextCompat
import androidx.media3.session.MediaBrowser
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject

@ActivityRetainedScoped
class ServiceConnection
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) {
        private val _connectionEvent =
            MutableSharedFlow<ServiceConnectionEvent>(replay = 1)
        val connectionEvent: SharedFlow<ServiceConnectionEvent>
            get() = _connectionEvent.asSharedFlow()
        val sessionToken =
            SessionToken(
                context,
                ComponentName(context, MusicalPlaybackService::class.java),
            )
        private val futureBrowser: ListenableFuture<MediaBrowser> =
            MediaBrowser.Builder(context, sessionToken).buildAsync().also {
                it.addListener({
                    _connectionEvent.tryEmit(ServiceConnectionEvent.Connected(it.get()))
                }, ContextCompat.getMainExecutor(context))
            }

        fun sendConnectedEvent() {
            if (futureBrowser.isDone) {
                _connectionEvent.tryEmit(ServiceConnectionEvent.Connected(futureBrowser.get()))
            } else {
                futureBrowser.addListener({
                    _connectionEvent.tryEmit(ServiceConnectionEvent.Connected(futureBrowser.get()))
                }, ContextCompat.getMainExecutor(context))
            }
        }

        fun sendDisconnectedEvent() {
            _connectionEvent.tryEmit(ServiceConnectionEvent.Disconnected)
        }

        fun destroyConnection() {
            MediaBrowser.releaseFuture(futureBrowser)
        }
    }

sealed interface ServiceConnectionEvent {
    object Disconnected : ServiceConnectionEvent

    data class Connected(val mediaBrowser: MediaBrowser) : ServiceConnectionEvent
}
