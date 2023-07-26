package rk.musical.ui.screen

import android.net.Uri
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
import androidx.compose.material3.CircularProgressIndicator
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
import rk.musical.utils.loadMediaCover

@Composable
fun SongsScreen(
    modifier: Modifier = Modifier,
    onSongClick: () -> Unit
) {
    val viewModel: SongsScreenViewModel = viewModel(factory = SongsScreenViewModel.Factory)
    val uiState = viewModel.uiState
    when (uiState) {
        SongsScreenUiState.Loading -> {
            Column(
                modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
            }
        }

        is SongsScreenUiState.Loaded -> {
            LazyColumn(
                modifier = modifier,
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                items(
                    items = uiState.songs,
                    key = { it.id }
                ) {
                    SongItem(song = it, onClick = onSongClick)
                }
            }
        }

        SongsScreenUiState.Empty -> {
            /*TODO: show empty screen*/
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongItem(
    song: Song,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            SubcomposeAsyncImage(
                model = loadMediaCover(LocalContext.current, song),
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
        id = 0,
        title = "This is song title",
        artist = "Artist name",
        albumId = 0,
        songUri = Uri.EMPTY

    )
    MusicalTheme {
        SongItem(
            song = songForPreview,
            onClick = { }
        )
    }

}