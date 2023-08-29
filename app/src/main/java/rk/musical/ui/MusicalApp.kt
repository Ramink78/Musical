package rk.musical.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import rk.musical.R
import rk.musical.navigation.MusicalRoutes
import rk.musical.ui.screen.AlbumsScreen
import rk.musical.ui.screen.PlayerScreen
import rk.musical.ui.screen.SongsScreen

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MusicalApp() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route ?: MusicalRoutes.Songs.name
    var isVisibleBottomBar by remember {
        mutableStateOf(true)
    }
    Scaffold(
        bottomBar = {
            MusicalBottomBar(
                isVisible = isVisibleBottomBar,
                currentRoute = currentRoute,
                onSelectedAlbums = { navController.navigate(MusicalRoutes.Albums.name) },
                onSelectedSongs = { navController.navigate(MusicalRoutes.Songs.name) },
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
            ) {
                PlayerScreen(
                    onSheetStateChange = {

                    },
                    behindContent = { sheetPadding ->
                        NavHost(
                            navController = navController,
                            startDestination = MusicalRoutes.Songs.name,
                        ) {
                            composable(route = MusicalRoutes.Songs.name) {
                                SongsScreen(onSongClick = {}, contentPadding = sheetPadding)
                            }
                            composable(route = MusicalRoutes.Albums.name) {
                                AlbumsScreen(contentPadding = sheetPadding)
                            }

                        }
                    })
            }

        }
    )

}

@Composable
fun MusicalBottomBar(
    modifier: Modifier = Modifier,
    currentRoute: String,
    onSelectedAlbums: () -> Unit,
    onSelectedSongs: () -> Unit,
    isVisible: Boolean = true
) {

    Column(modifier = modifier) {
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            NavigationBar {
                NavigationBarItem(
                    selected = currentRoute == MusicalRoutes.Songs.name,
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
                NavigationBarItem(selected = currentRoute == MusicalRoutes.Albums.name,
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
