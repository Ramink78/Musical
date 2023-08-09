package rk.musical.ui.screen

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material3.FilledIconToggleButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import rk.musical.data.model.Song
import rk.musical.player.MusicalServiceConnection
import rk.musical.utils.loadCover
import rk.musical.utils.readableDuration

sealed class PlayerUiState {
    object Collapsed : PlayerUiState()
    object Expanded : PlayerUiState()
}

@Composable
fun FullScreenPlayer(
    playingSong: Song,
    onProgressChanged: (Float) -> Unit,
    progress: Float,
    onCollapseClicked: () -> Unit,
    onPlayClicked: (Boolean) -> Unit,
    isPlaying: Boolean,
    remainingTime: String
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp))
            .padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconButton(
            onClick = onCollapseClicked,
            modifier = Modifier
                .align(Alignment.Start)
        ) {
            Icon(imageVector = Icons.Rounded.KeyboardArrowDown, contentDescription = "")
        }
        Spacer(modifier = Modifier.height(4.dp))
        FullScreenPlayerImage(
            imageRequest = playingSong.loadCover(
                LocalContext.current
            )
        )


        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .weight(1f)
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            FullScreenPlayerInfo(
                title = playingSong.title,
                subtitle = playingSong.artist,
                modifier = Modifier.fillMaxWidth()

            )
            Spacer(modifier = Modifier.height(8.dp))
            FullScreenPlayerSlider(
                totalTime = readableDuration(playingSong.duration),
                remainingTime = remainingTime,
                progress = progress,
                onProgressChanged = onProgressChanged
            )
            FullScreenPlayerControls(
                onPlayChanged = onPlayClicked,
                isPlaying = isPlaying
            )
        }


    }

}

@Composable
fun FullScreenPlayerImage(imageRequest: ImageRequest?, modifier: Modifier = Modifier) {
    AsyncImage(
        model = imageRequest,
        contentDescription = "",
        contentScale = ContentScale.Crop,
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(12.dp))
    )
}

@Composable
fun FullScreenPlayerInfo(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = title,
            modifier = Modifier.padding(bottom = 4.dp),
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall
        )

    }

}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun FullScreenPlayerControls(
    modifier: Modifier = Modifier,
    onPlayChanged: (Boolean) -> Unit,
    onSkipNext: () -> Unit = {},
    onSkipPrevious: () -> Unit = {},
    isPlaying: Boolean
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(
            onClick = onSkipPrevious,
            modifier = Modifier
                .size(44.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.SkipPrevious, contentDescription = "",
                modifier = Modifier.size(34.dp)

            )
        }
        FilledIconToggleButton(
            checked = isPlaying,
            onCheckedChange = onPlayChanged,
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .size(68.dp)
        ) {
            AnimatedContent(targetState = isPlaying, label = "") {
                if (it)
                    Icon(
                        imageVector = Icons.Rounded.Pause, contentDescription = "",
                        modifier = Modifier.size(36.dp)
                    )
                else
                    Icon(
                        imageVector = Icons.Rounded.PlayArrow, contentDescription = "",
                        modifier = Modifier.size(36.dp)
                    )
            }

        }
        IconButton(
            onClick = onSkipNext,
            modifier = Modifier
                .size(44.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.SkipNext, contentDescription = "",
                modifier = Modifier.size(34.dp)

            )
        }

    }
}

@Composable
fun FullScreenPlayerSlider(
    modifier: Modifier = Modifier,
    remainingTime: String,
    totalTime: String,
    progress: Float,
    onProgressChanged: (Float) -> Unit
) {
    Column(modifier = modifier) {
        Slider(value = progress, onValueChange = onProgressChanged)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = remainingTime, style = MaterialTheme.typography.bodySmall)
            Text(text = totalTime, style = MaterialTheme.typography.bodySmall)
        }

    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun NowPlayingScreen(
    onStateChange: (playerState: PlayerUiState) -> Unit,
    musicalServiceConnection: MusicalServiceConnection,
) {
    val viewModel: NowPlayingScreenViewModel =
        viewModel(factory = NowPlayingScreenViewModel.Factory(musicalServiceConnection))
    val uiState = viewModel.uiState
    BackHandler(enabled = uiState.isFullScreen) {
        viewModel.toggleFullScreen()
    }
    AnimatedContent(
        targetState = uiState.isFullScreen,
        label = "",
        transitionSpec = {
            ContentTransform(
                targetContentEnter = slideIntoContainer(towards = AnimatedContentScope.SlideDirection.Up) + fadeIn(),
                initialContentExit = slideOutOfContainer(towards = AnimatedContentScope.SlideDirection.Down) + fadeOut()
            )
        }
    ) {
        if (it) {
            onStateChange(PlayerUiState.Expanded)
            FullScreenPlayer(
                onCollapseClicked = {
                    viewModel.toggleFullScreen()
                },
                playingSong = uiState.playingSong,
                onPlayClicked = { playing ->
                    if (playing) {
                        viewModel.resume()
                    } else {
                        viewModel.pause()
                    }
                },
                isPlaying = uiState.isPlaying,
                remainingTime = uiState.remainingTime,
                progress = uiState.progress,
                onProgressChanged = { progress ->
                    viewModel.seekTo(progress)
                }
            )
        } else {
            onStateChange(PlayerUiState.Collapsed)
            MiniPlayer(
                onClick = {
                    viewModel.toggleFullScreen()
                },
                playingSong = uiState.playingSong,
                onPlayPauseClicked = {
                    if (uiState.isPlaying) {
                        viewModel.pause()
                    } else {
                        viewModel.resume()
                    }
                },
                isPlaying = uiState.isPlaying
            )
        }
    }

}

@Composable
fun MiniPlayer(
    onClick: () -> Unit,
    playingSong: Song,
    onPlayPauseClicked: () -> Unit,
    isPlaying: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp)
            .clip(
                RoundedCornerShape(
                    topStart = 10.dp,
                    topEnd = 10.dp
                )
            )
            .background(MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp))
            .clickable { onClick() }
            .padding(12.dp),

        verticalAlignment = Alignment.CenterVertically

    ) {

        AsyncImage(
            model = playingSong.loadCover(LocalContext.current),
            contentDescription = "",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)

        )
        Column(
            modifier = Modifier
                .padding(start = 12.dp)
                .weight(1f)
        ) {
            Text(
                text = playingSong.title,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = playingSong.artist,
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        IconButton(onClick = onPlayPauseClicked) {
            Icon(
                imageVector =
                if (isPlaying)
                    Icons.Rounded.Pause
                else
                    Icons.Rounded.PlayArrow,
                contentDescription = ""
            )
        }


    }
}

@Preview
@Composable
fun MiniPlayerPreview() {
    val playingSong = Song(
        id = "0",
        title = "This is song title",
        artist = "Artist name",
        albumId = "0",
        songUri = "",
        albumName = "",
        duration = 0
    )
    MiniPlayer(
        playingSong = playingSong,
        onClick = {},
        onPlayPauseClicked = {},
        isPlaying = false,
    )
}

@Preview
@Composable
fun FullScreenPlayerPreview() {
    val playingSong = Song(
        id = "0",
        title = "This is song title",
        artist = "Artist name",
        albumId = "0",
        songUri = "",
        albumName = "",
        duration = 0
    )
    FullScreenPlayer(
        onCollapseClicked = {},
        playingSong = playingSong,
        onPlayClicked = {},
        isPlaying = false,
        remainingTime = "",
        progress = 0f,
        onProgressChanged = {}
    )
}