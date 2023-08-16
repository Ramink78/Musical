package rk.musical.ui.screen

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import rk.musical.data.model.Album
import rk.musical.ui.theme.MusicalTheme
import rk.musical.ui.theme.Purple40
import rk.musical.ui.theme.PurpleGrey80
import rk.musical.utils.loadCover

@Composable
fun AlbumsScreen(
    modifier: Modifier = Modifier,
) {
    val viewModel: AlbumsScreenViewModel = hiltViewModel()
    val uiState = viewModel.uiState
    val albums = viewModel.albums
    val albumChildren = viewModel.albumChildren
    BackHandler(uiState is AlbumsScreenUiState.LoadedChildren) {
        viewModel.navigateBackToAlbums()
    }
    Crossfade(targetState = uiState, label = "") { state ->
        when (state) {
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
                    onAlbumClicked = { album ->
                        viewModel.loadAlbumChildren(album)
                    }
                )
            }

            is AlbumsScreenUiState.LoadedChildren -> {
                SongsList(songs = albumChildren, onSongClick = { song ->
                    viewModel.play(song)
                }, modifier = modifier)
            }

            AlbumsScreenUiState.Empty -> {}
        }

    }


}

@Composable
fun AlbumsList(
    albums: List<Album>,
    onAlbumClicked: (Album) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
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
    modifier: Modifier = Modifier,
) {
    ElevatedCard(
        onClick = onClick,
        modifier = modifier
    ) {
        Column {

            AsyncImage(
                model = album.loadCover(LocalContext.current),
                contentDescription = "",
                modifier = Modifier
                    .fillMaxSize()
                    .height(140.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier.padding(8.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = album.title,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 4.dp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = album.artist,
                    style = MaterialTheme.typography.bodySmall,
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
    val albumForPreview = Album(
        id = "0",
        title = "Album name",
        artist = "Artist Name",
        songsCount = 5
    )
    MusicalTheme {
        AlbumItem(onClick = {}, album = albumForPreview)
    }
}
