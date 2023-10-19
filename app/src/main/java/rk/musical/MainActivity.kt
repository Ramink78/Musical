package rk.musical

import android.media.AudioManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import dagger.hilt.android.AndroidEntryPoint
import rk.musical.player.ServiceConnection
import rk.musical.ui.MusicalApp
import rk.musical.ui.theme.MusicalTheme
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var serviceConnection: ServiceConnection

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        volumeControlStream = AudioManager.STREAM_MUSIC
        setContent {
            MusicalTheme() {
                MusicalApp()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        serviceConnection.sendConnectedEvent()
    }

    override fun onStop() {
        super.onStop()
        serviceConnection.sendDisconnectedEvent()
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceConnection.destroyConnection()
    }
}
