package rk.musical.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import rk.musical.R
import rk.musical.ui.screen.AlbumsScreen
import rk.musical.ui.screen.SongsScreen
import rk.musical.ui.theme.MusicalTheme

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MusicalApp() {
    var currentScreen: Int by remember {
        mutableStateOf(0)
    }
    Scaffold(
        bottomBar = {
            MusicalBottomNavigation(currentScreen = currentScreen,
                onSelectedAlbums = { currentScreen = 1 },
                onSelectedSongs = { currentScreen = 0 })
        },
        content = { paddingValues ->
            AnimatedContent(targetState = currentScreen) {
                when (it) {
                    0 -> {
                        SongsScreen(
                            modifier = Modifier
                                .padding(paddingValues)
                                .padding(
                                    start = 8.dp,
                                    end = 8.dp,
                                    top = 16.dp
                                ),
                            onSongClick = {}
                        )
                    }

                    1 -> {
                        AlbumsScreen(
                            modifier = Modifier.padding(paddingValues)

                        )
                    }
                }
            }


        }
    )
}

@Composable
fun MusicalBottomNavigation(
    modifier: Modifier = Modifier,
    currentScreen: Int,
    onSelectedAlbums: () -> Unit,
    onSelectedSongs: () -> Unit,
) {

    NavigationBar(modifier = modifier) {
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
        NavigationBarItem(selected = currentScreen == 1, onClick = onSelectedAlbums, icon = {
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

@Preview
@Composable
fun MusicalBottomNavigationPreview() {
    MusicalTheme {
        MusicalApp()
    }

}