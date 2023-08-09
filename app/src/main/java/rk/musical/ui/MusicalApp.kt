package rk.musical.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import rk.musical.R
import rk.musical.player.MusicalServiceConnection
import rk.musical.ui.screen.AlbumsScreen
import rk.musical.ui.screen.NowPlayingScreen
import rk.musical.ui.screen.PlayerUiState
import rk.musical.ui.screen.SongsScreen

@Composable
fun MusicalApp(
    musicalViewModel: MusicalAppViewModel,
    musicalServiceConnection: MusicalServiceConnection
) {
    var currentScreen: Int by remember {
        mutableStateOf(0)
    }
    val musicalPlaybackState = musicalViewModel.musicalPlaybackState

    Scaffold(
        bottomBar = {
            MusicalBottomBar(
                currentScreen = currentScreen,
                onSelectedAlbums = { currentScreen = 1 },
                onSelectedSongs = { currentScreen = 0 },
                musicalServiceConnection = musicalServiceConnection,
                playingSong = musicalPlaybackState.playingMediaItem,
            )
        },
        content = { paddingValues ->
            when (currentScreen) {
                0 -> {
                    SongsScreen(
                        musicalServiceConnection = musicalServiceConnection,
                        modifier = Modifier
                            .padding(paddingValues)
                            .padding(
                                start = 8.dp,
                                end = 8.dp,
                                top = 16.dp
                            ),
                    )
                }

                1 -> {
                    AlbumsScreen(
                        modifier = Modifier.padding(paddingValues),
                        musicalServiceConnection = musicalServiceConnection
                    )
                }
            }


        }
    )

}

@Composable
fun MusicalBottomBar(
    modifier: Modifier = Modifier,
    playingSong: MediaItem = MediaItem.EMPTY,
    currentScreen: Int,
    onSelectedAlbums: () -> Unit,
    onSelectedSongs: () -> Unit,
    musicalServiceConnection: MusicalServiceConnection
) {
    var isShowNavigationBar by remember {
        mutableStateOf(true)
    }
    Column(modifier = modifier) {
        AnimatedVisibility(visible = playingSong != MediaItem.EMPTY) {
            NowPlayingScreen(musicalServiceConnection = musicalServiceConnection,
                onStateChange = {
                    isShowNavigationBar = when (it) {
                        PlayerUiState.Expanded -> false
                        PlayerUiState.Collapsed -> true
                    }
                }
            )
        }
        AnimatedVisibility(
            visible = isShowNavigationBar,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            NavigationBar {
                NavigationBarItem(
                    selected = currentScreen == 0,
                    onClick = onSelectedSongs,
                    icon = {
                        Icon(
                            imageVector = Icons.Default.MusicNote,
                            contentDescription = stringResource(R.string.songs_tab_cd)
                        )
                    },
                    label = {
                        Text(text = stringResource(R.string.songs))
                    }
                )
                NavigationBarItem(selected = currentScreen == 1,
                    onClick = onSelectedAlbums,
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Album,
                            contentDescription = stringResource(R.string.albums_tab_cd)
                        )
                    },
                    label = {
                        Text(text = stringResource(R.string.albums))
                    })

            }
        }

    }

}
