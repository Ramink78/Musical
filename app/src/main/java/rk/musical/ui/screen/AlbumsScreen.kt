package rk.musical.ui.screen

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Album
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.collections.immutable.toImmutableList
import rk.musical.R
import rk.musical.data.model.Album
import rk.musical.ui.RationaleWarning
import rk.musical.ui.RequiredMediaPermission
import rk.musical.ui.component.AlbumPlaceholder
import rk.musical.ui.mediaPermission
import rk.musical.ui.theme.MusicalTheme
import rk.musical.ui.theme.Purple40
import rk.musical.ui.theme.PurpleGrey80

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun AlbumsScreen(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    onItemClick: (Album) -> Unit
) {
    val context = LocalContext.current
    val viewModel: AlbumsScreenViewModel = hiltViewModel()
    val uiState = viewModel.uiState
    val albums = viewModel.albums
    val albumChildren = viewModel.albumChildren
    val permissionState = rememberPermissionState(permission = mediaPermission)

    RequiredMediaPermission(
        permissionState = permissionState,
        grantedContent = {
            LaunchedEffect(Unit) {
                viewModel.refreshAlbums()
            }
            when (uiState) {
                AlbumsScreenUiState.Loading -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        LoadingCircle()
                    }
                }

                is AlbumsScreenUiState.Loaded, AlbumsScreenUiState.NavigateBack -> {
                    AlbumsList(
                        albums = albums,
                        modifier = modifier,
                        onAlbumClicked = onItemClick,
                        contentPadding = contentPadding
                    )
                }

                is AlbumsScreenUiState.LoadedChildren -> {
                    SongsList(
                        songs = albumChildren.toImmutableList(),
                        onSongClick = { index ->
                        },
                        modifier = modifier,
                        contentPadding = contentPadding
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
                    rationaleText = stringResource(R.string.albums_permission_rationale),
                    icon = Icons.Rounded.Album,
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
                    icon = Icons.Rounded.Album,
                    rationaleText = stringResource(R.string.albums_permission_rationale),
                    rationaleTitle = stringResource(R.string.media_permission_title)
                )
            }
        }
    )
}

@Composable
fun AlbumsList(
    albums: List<Album>,
    contentPadding: PaddingValues = PaddingValues(),
    onAlbumClicked: (Album) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        modifier = modifier,
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = contentPadding
    ) {
        items(
            items = albums,
            key = { it.id }
        ) {
            AlbumItem(
                onClick = {
                    onAlbumClicked(it)
                },
                album = it
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumItem(
    onClick: () -> Unit,
    album: Album,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        onClick = onClick,
        modifier = modifier
    ) {
        Column {
            CoverImage(
                coverUri = album.coverUri,
                modifier =
                Modifier
                    .fillMaxSize()
                    .height(140.dp)
                    .clip(
                        RoundedCornerShape(8.dp)
                    ),
                placeholder = { AlbumPlaceholder() }
            )

            Column(
                modifier = Modifier.padding(8.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = album.title,
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(bottom = 4.dp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = album.artist,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun LoadingCircle() {
    CircularProgressIndicator(
        strokeCap = StrokeCap.Round,
        trackColor = PurpleGrey80,
        color = Purple40,
        strokeWidth = 6.dp,
        modifier = Modifier.size(60.dp)
    )
}

@Preview
@Composable
fun AlbumItemPreview() {
    val albumForPreview =
        Album(
            id = "0",
            title = "Album name",
            artist = "Artist Name",
            songsCount = 5
        )
    MusicalTheme {
        AlbumItem(onClick = {}, album = albumForPreview)
    }
}
