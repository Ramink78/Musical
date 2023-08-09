package rk.musical

import android.content.ComponentName
import android.media.AudioManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import rk.musical.player.MusicalPlaybackService
import rk.musical.player.MusicalServiceConnection
import rk.musical.ui.MusicalApp
import rk.musical.ui.MusicalAppViewModel
import rk.musical.ui.theme.MusicalTheme

class MainActivity : ComponentActivity() {
    private val musicalServiceConnection: MusicalServiceConnection by lazy {
        MusicalServiceConnection.getInstance(
            context = applicationContext,
            ComponentName(this, MusicalPlaybackService::class.java)
        )
    }
    private val musicalViewModel: MusicalAppViewModel by viewModels {
        MusicalAppViewModel.Companion.Factory(musicalServiceConnection)
    }


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
                        musicalServiceConnection = musicalServiceConnection,
                        musicalViewModel = musicalViewModel
                    )
                }
            }
        }
    }


}
