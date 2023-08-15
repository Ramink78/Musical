package rk.musical.ui.screen

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.SubcomposeAsyncImage
import rk.musical.data.model.Song
import rk.musical.ui.theme.MusicalTheme
import rk.musical.utils.loadCover

@Composable
fun SongsScreen(
    modifier: Modifier = Modifier
) {
    val viewModel: SongsScreenViewModel = viewModel()
    val uiState = viewModel.uiState
    Crossfade(targetState = uiState, label = "") {
        when (it) {
            SongsScreenUiState.Loading -> {
                Column(
                    modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    LoadingCircle()
                }
            }

            is SongsScreenUiState.Loaded -> {
                SongsList(modifier = modifier, songs = it.songs, onSongClick = {
                    viewModel.playSong(it)
                })
            }

            else -> {
                // Show empty screen
            }
        }

    }


}

@Composable
fun SongsList(
    songs: List<Song>,
    modifier: Modifier = Modifier,
    onSongClick: (Song) -> Unit
) {

    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        items(
            items = songs,
            key = { it.id }
        ) {
            SongItem(song = it, onClick = { song ->
                onSongClick(song)
            })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongItem(
    song: Song,
    onClick: (Song) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        onClick = {
            onClick(song)
        }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            SubcomposeAsyncImage(
                model = song.loadCover(LocalContext.current),
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape),
                error = {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .background(Color.LightGray),
                        contentAlignment = Alignment.Center

                    ) {
                        Text(
                            text = "${song.title.first().uppercaseChar()}",
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color.DarkGray
                        )

                    }
                }

            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(12.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = song.title,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 4.dp),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = song.artist,
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

    }

}

@Preview
@Composable
fun SongsScreenPreview() {
    val songForPreview = Song(
        id = "0",
        title = "This is song title",
        artist = "Artist name",
        songUri = "",
        albumName = "",
        duration = 0
    )
    MusicalTheme {
        SongItem(
            song = songForPreview,
            onClick = { }
        )
    }

}