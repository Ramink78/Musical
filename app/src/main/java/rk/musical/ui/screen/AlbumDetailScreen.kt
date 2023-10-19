package rk.musical.ui.screen

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import rk.musical.data.model.Song
import rk.musical.ui.component.AlbumPlaceholder
import rk.musical.ui.theme.MusicalTheme

@Composable
fun AlbumDetailScreen(albumId: String) {
    val viewModel: AlbumDetailScreenViewModel = hiltViewModel()
    val album =
        remember {
            viewModel.findAlbumById(albumId)!!
        }

    val playingSong by viewModel.playingSong.collectAsStateWithLifecycle()
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding =
        PaddingValues(
            bottom = 180.dp
        )
    ) {
        item {
            AlbumHeader(
                Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(bottomStart = 18.dp, bottomEnd = 18.dp)),
                coverUri = album.coverUri
            )
        }
        item {
            AlbumInfo(
                title = album.title,
                subtitle = album.artist,
                modifier = Modifier.padding(16.dp)
            )
        }
        itemsIndexed(viewModel.getAlbumChildren(albumId)) { index, item ->
            AlbumChildItem(
                modifier = Modifier.padding(horizontal = 8.dp),
                song = item,
                onItemClick = { viewModel.playSong(index) },
                ordinal = index + 1,
                isChecked = playingSong == item
            )
        }
    }
}

@Composable
private fun AlbumInfo(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String
) {
    Row {
        Column(
            modifier = modifier
        ) {
            Text(
                text = title,
                modifier = Modifier.padding(bottom = 4.dp),
                style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelMedium,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun AlbumHeader(
    modifier: Modifier = Modifier,
    coverUri: String?
) {
    CoverImage(
        coverUri = coverUri,
        modifier = modifier,
        placeholder = {
            AlbumPlaceholder()
        }
    )
}

@Composable
private fun AlbumChildItem(
    modifier: Modifier = Modifier,
    song: Song = Song.Empty,
    ordinal: Int = 1,
    isChecked: Boolean = false,
    onItemClick: () -> Unit
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = if (ordinal < 10) "0$ordinal" else "$ordinal",
            style = MaterialTheme.typography.bodyLarge,
            maxLines = 1,
            modifier = Modifier.padding(start = 12.dp),
            color = MaterialTheme.colorScheme.primary
        )
        ChildItem(song = song, onClick = onItemClick, isChecked = isChecked)
    }
}

@Composable
fun ChildItem(
    song: Song,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isChecked: Boolean = false
) {
    val cardBackgroundColor by animateColorAsState(
        targetValue =
        if (isChecked) {
            MaterialTheme.colorScheme.primary
        } else {
            Color.Transparent
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

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier =
        modifier
            .fillMaxWidth()
            .padding(12.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .drawBehind {
                drawRect(cardBackgroundColor)
            }
            .graphicsLayer {
                scaleY = cardScale
                scaleX = cardScale
            }
    ) {
        Column(
            modifier =
            Modifier
                .weight(1f)
                .padding(12.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = song.title,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 4.dp),
                maxLines = 1,
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

@Preview
@Composable
fun AlbumDetailPreview() {
    MusicalTheme(darkTheme = true) {
        AlbumDetailScreen("")
    }
}
