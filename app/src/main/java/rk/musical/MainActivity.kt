package rk.musical

import android.media.AudioManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
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
        volumeControlStream = AudioManager.STREAM_MUSIC
        setContent {
            MusicalTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MusicalApp()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        serviceConnection.connect(this)
    }

    override fun onStop() {
        super.onStop()
        serviceConnection.disconnect()
    }


}
