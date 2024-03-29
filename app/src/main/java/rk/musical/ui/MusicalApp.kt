package rk.musical.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import rk.musical.R
import rk.musical.navigation.MusicalRoutes
import rk.musical.ui.screen.AlbumDetailScreen
import rk.musical.ui.screen.AlbumsScreen
import rk.musical.ui.screen.PlayerScreen
import rk.musical.ui.screen.SongsScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicalApp() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route ?: MusicalRoutes.Songs.name
    val sheetState = rememberBottomSheetScaffoldState()
    val navigateToAlbumsScreen =
        remember {
            {
                navController.navigate(MusicalRoutes.Albums.name) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        }
    val navigateToSongsScreen =
        remember {
            {
                navController.navigate(MusicalRoutes.Songs.name) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        }
    Scaffold(
        bottomBar = {
            MusicalBottomBar(
                currentRoute = currentRoute,
                onSelectedAlbums = navigateToAlbumsScreen,
                onSelectedSongs = navigateToSongsScreen,
                isVisible = sheetState.bottomSheetState.targetValue == SheetValue.PartiallyExpanded
            )
        }
    ) { innerPadding ->
        val sheetPeakHeight = innerPadding.calculateBottomPadding() + 60.dp
        PlayerScreen(
            sheetState = sheetState,
            sheetPeakHeight = sheetPeakHeight,
            behindContent = { sheetPadding ->
                NavHost(
                    modifier = Modifier.background(MaterialTheme.colorScheme.background),
                    navController = navController,
                    startDestination = MusicalRoutes.Songs.name
                ) {
                    composable(route = MusicalRoutes.Songs.name) {
                        SongsScreen(
                            contentPadding =
                            PaddingValues(
                                top =
                                WindowInsets.statusBars.asPaddingValues()
                                    .calculateTopPadding(),
                                bottom = sheetPadding.calculateBottomPadding(),
                                end = 8.dp,
                                start = 8.dp
                            ),
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    composable(route = MusicalRoutes.Albums.name) {
                        AlbumsScreen(
                            contentPadding =
                            PaddingValues(
                                bottom = sheetPadding.calculateBottomPadding(),
                                top =
                                WindowInsets.statusBars.asPaddingValues()
                                    .calculateTopPadding()
                            ),
                            modifier = Modifier.fillMaxSize(),
                            onItemClick = {
                                navController.navigate(
                                    "${MusicalRoutes.AlbumDetail.name}/${it.id}"
                                )
                            }
                        )
                    }
                    composable(
                        route = "${MusicalRoutes.AlbumDetail.name}/{albumId}",
                        arguments =
                        listOf(
                            navArgument("albumId") { type = NavType.StringType }
                        )
                    ) {
                        AlbumDetailScreen(
                            it.arguments?.getString("albumId") ?: ""
                        )
                    }
                }
            }
        )
    }
}

@Composable
fun MusicalBottomBar(
    modifier: Modifier = Modifier,
    currentRoute: String,
    onSelectedAlbums: () -> Unit,
    onSelectedSongs: () -> Unit,
    isVisible: Boolean = true
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn() + slideInVertically { it },
        exit = fadeOut() + slideOutVertically { it }
    ) {
        NavigationBar(
            modifier = modifier,
            tonalElevation = 0.dp
        ) {
            NavigationBarItem(
                selected = currentRoute == MusicalRoutes.Songs.name,
                onClick = onSelectedSongs,
                icon = {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = stringResource(R.string.songs_tab_cd)
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    selectedIconColor = MaterialTheme.colorScheme.surface,
                    indicatorColor = MaterialTheme.colorScheme.primary
                ),
                label = {
                    Text(
                        text = stringResource(R.string.songs),
                        style =
                        if (currentRoute == MusicalRoutes.Songs.name) {
                            MaterialTheme.typography.headlineMedium.copy(fontSize = 14.sp)
                        } else {
                            MaterialTheme.typography.bodyMedium
                        }
                    )
                }
            )
            NavigationBarItem(
                selected = currentRoute == MusicalRoutes.Albums.name,
                onClick = onSelectedAlbums,
                icon = {
                    Icon(
                        imageVector = Icons.Default.Album,
                        contentDescription = stringResource(R.string.albums_tab_cd)
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    selectedIconColor = MaterialTheme.colorScheme.surface,
                    indicatorColor = MaterialTheme.colorScheme.primary
                ),
                label = {
                    Text(
                        text = stringResource(R.string.albums),
                        style =
                        if (currentRoute == MusicalRoutes.Albums.name) {
                            MaterialTheme.typography.headlineMedium.copy(fontSize = 14.sp)
                        } else {
                            MaterialTheme.typography.bodyMedium
                        }
                    )
                }
            )
        }
    }
}
