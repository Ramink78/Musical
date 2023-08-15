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
import rk.musical.player.MusicalServiceConnection
import rk.musical.ui.MusicalApp
import rk.musical.ui.MusicalAppViewModel
import rk.musical.ui.theme.MusicalTheme
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var serviceConnection: MusicalServiceConnection
    private val musicalViewModel: MusicalAppViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        volumeControlStream = AudioManager.STREAM_MUSIC
        setContent {
            MusicalTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MusicalApp(
                        musicalViewModel = musicalViewModel
                    )
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        serviceConnection.connectToService(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceConnection.releaseConnection()
    }


}
