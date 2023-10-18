package rk.musical.ui.screen

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch
import rk.musical.R
import rk.musical.data.model.Song
import rk.musical.ui.RationaleWarning
import rk.musical.ui.RequiredMediaPermission
import rk.musical.ui.component.SongPlaceholder
import rk.musical.ui.mediaPermission
import rk.musical.ui.theme.MusicalTheme
import rk.musical.ui.theme.Purple40

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun SongsScreen(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues()
) {
    val context = LocalContext.current
    val viewModel: SongsScreenViewModel = hiltViewModel()
    val uiState = viewModel.uiState
    val permissionState = rememberPermissionState(permission = mediaPermission)
    val playingSong = viewModel.playingSongFlow.collectAsStateWithLifecycle()
    val currentSong: () -> Song = remember {
        { playingSong.value }
    }

    RequiredMediaPermission(
        permissionState = permissionState,
        grantedContent = {
            LaunchedEffect(Unit) {
                viewModel.refreshSongs()
            }
            when (uiState) {
                SongsScreenUiState.Loading -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        LoadingCircle()
                    }
                }

                is SongsScreenUiState.Loaded -> {
                    val scope = rememberCoroutineScope()
                    val onSongClick: (Song) -> Unit = remember {
                        { viewModel.playSong(uiState.songs.indexOf(it)) }
                    }
                    val listState = rememberLazyListState()

                    val isVisibleGoButton by remember {
                        derivedStateOf {
                            (listState.firstVisibleItemIndex > 0) &&
                                    playingSong.value != Song.Empty
                        }
                    }
                    SongsList(
                        modifier = modifier,
                        songs = uiState.songs.toImmutableList(),
                        contentPadding = contentPadding,
                        onSongClick = onSongClick,
                        playingSong = currentSong,
                        listState = listState,
                        isVisibleGoButton = isVisibleGoButton,
                        onGotoClick = {
                            scope.launch {
                                listState.animateScrollToItem(
                                    index = uiState.songs.indexOf(
                                        playingSong.value
                                    )
                                )
                            }
                        }
                    )
                }

                else -> {}
            }
        },
        rationalContent = {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                RationaleWarning(
                    onRequest = { permissionState.launchPermissionRequest() },
                    buttonText = "Request",
                    rationaleText = stringResource(R.string.songs_permission_rationale),
                    icon = Icons.Rounded.MusicNote,
                    rationaleTitle = stringResource(R.string.media_permission_title)
                )
            }
        },
        deniedContent = {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                RationaleWarning(
                    onRequest = {
                        context.startActivity(
                            Intent(
                                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", context.packageName, null)
                            )
                        )
                    },
                    buttonText = "Grant in setting",
                    icon = Icons.Rounded.MusicNote,
                    rationaleText = stringResource(R.string.songs_permission_rationale),
                    rationaleTitle = stringResource(R.string.media_permission_title)
                )
            }
        }
    )
}

@Composable
fun SongsList(
    songs: ImmutableList<Song>,
    modifier: Modifier = Modifier,
    playingSong: () -> Song = { Song.Empty },
    onSongClick: (Song) -> Unit,
    isVisibleGoButton: Boolean = false,
    onGotoClick: () -> Unit,
    listState: LazyListState,
    contentPadding: PaddingValues = PaddingValues()
) {
    Box(modifier = modifier) {
        LazyColumn(
            state = listState,
            verticalArrangement = Arrangement.spacedBy(4.dp),
            contentPadding = contentPadding
        ) {
            items(
                items = songs,
                key = {
                    it.id
                }
            ) {
                SongItem(
                    song = it,
                    onClick = onSongClick,
                    isChecked = it.id == playingSong().id
                )
            }
        }
        AnimatedVisibility(
            visible = isVisibleGoButton,
            enter = slideInVertically(initialOffsetY = { -it }),
            exit = slideOutVertically(targetOffsetY = { -it })
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(
                    modifier = Modifier.height(
                        WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
                    )
                )
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(color = Purple40.copy(.4f), shape = CircleShape)
                        .clickable {
                            onGotoClick()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.MusicNote,
                        contentDescription = "",
                        modifier = Modifier.size(28.dp),
                        tint = contentColorFor(backgroundColor = Purple40.copy(.4f))
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongItem(
    song: Song,
    onClick: (Song) -> Unit,
    modifier: Modifier = Modifier,
    isChecked: Boolean = false
) {
    val cardBackgroundColor by animateColorAsState(
        targetValue =
        if (isChecked) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.surfaceVariant
        },
        label = "",
        animationSpec = tween(400)
    )
    val cardContentColor by animateColorAsState(
        targetValue =
        if (isChecked) {
            contentColorFor(
                MaterialTheme.colorScheme.primary
            )
        } else {
            contentColorFor(MaterialTheme.colorScheme.surfaceVariant)
        },
        label = "",
        animationSpec = tween(400)
    )
    val cardScale by animateFloatAsState(
        targetValue = if (isChecked) .95f else 1f,
        label = "",
        animationSpec = tween(400)
    )

    Card(
        modifier =
        modifier
            .graphicsLayer {
                scaleY = cardScale
                scaleX = cardScale
            },
        onClick = { onClick(song) },
        colors = CardDefaults.cardColors(containerColor = cardBackgroundColor)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier =
            Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            CoverImage(
                coverUri = song.coverUri,
                modifier =
                Modifier
                    .size(60.dp)
                    .clip(CircleShape),
                placeholder = { SongPlaceholder() }
            )
            Column(
                modifier =
                Modifier
                    .weight(1f)
                    .padding(12.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = song.title,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 4.dp),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = cardContentColor
                )
                Text(
                    text = song.artist,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = cardContentColor
                )
            }
        }
    }
}

@Preview
@Composable
fun SongsScreenPreview() {
    val songForPreview =
        Song(
            id = "0",
            title = "This is song title",
            artist = "Artist name",
            songUri = "",
            albumName = "",
            duration = 0
        )
    MusicalTheme(darkTheme = true) {
        SongItem(
            song = songForPreview,
            onClick = { }
        )
    }
}
