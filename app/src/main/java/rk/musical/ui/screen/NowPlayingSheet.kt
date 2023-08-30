package rk.musical.ui.screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.BottomSheetState
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material3.FilledIconToggleButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import rk.musical.R
import rk.musical.utils.loadCover
import rk.musical.utils.readableDuration

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PlayerScreen(
    onSheetStateChange: (BottomSheetValue, Float) -> Unit,
    behindContent: @Composable (PaddingValues) -> Unit
) {
    val viewModel: NowPlayingScreenViewModel = hiltViewModel()
    val uiState = viewModel.uiState
    PlayerScreen(
        onSeekFinished = {
            viewModel.seekTo(it)
        },
        onSeekValueChange = {
            viewModel.updateProgress(it)
        },
        onSkipNext = {viewModel.skipToNext()},
        onSkipPrevious = {viewModel.skipToPrevious()},
        title = uiState.playingSong.title,
        subtitle = uiState.playingSong.artist,
        isPlaying = uiState.isPlaying,
        remainingTime = uiState.currentTime,
        totalTime = readableDuration(uiState.playingSong.duration),
        onPlayPauseClicked = {
            viewModel.togglePlay()
        },
        imagePainter =
        rememberAsyncImagePainter(
            model = uiState.playingSong.loadCover(LocalContext.current)
        ),
        progress = uiState.progress,
        isSheetVisible = uiState.isVisible,
        onSheetStateChange = { value, progress ->
            onSheetStateChange(value, progress)
            viewModel.setExpanded(value == BottomSheetValue.Expanded)
        },
        behindContent = behindContent
    )

}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun PlayerScreen(
    title: String,
    subtitle: String,
    isPlaying: Boolean,
    remainingTime: String,
    totalTime: String,
    onPlayPauseClicked: () -> Unit,
    onSeekValueChange: (Float) -> Unit,
    imagePainter: Painter,
    onSeekFinished: ((Float) -> Unit)?,
    progress: Float,
    isSheetVisible: Boolean,
    onSheetStateChange: (BottomSheetValue, Float) -> Unit,
    onSkipNext: () -> Unit,
    onSkipPrevious: () -> Unit,
    behindContent: @Composable (PaddingValues) -> Unit
) {
    val scaffoldState = rememberBottomSheetScaffoldState()
    val sheetPeekHeight by animateDpAsState(
        targetValue =
        if (isSheetVisible) 72.dp else 0.dp, label = ""
    )
    BottomSheetScaffold(
        sheetShape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        sheetContent = {
            Box(modifier = Modifier.fillMaxSize()) {
                Crossfade(
                    targetState = scaffoldState.bottomSheetState.calculateProgress() >= .25f,
                    label = ""
                ) {
                    if (it) {
                        ExpandedPlayer(
                            title = title,
                            subtitle = subtitle,
                            imagePainter = imagePainter,
                            onPlayPauseClicked = onPlayPauseClicked,
                            isPlaying = isPlaying,
                            remainingTime = remainingTime,
                            totalTime = totalTime,
                            progress = progress,
                            onSeekValueChange = onSeekValueChange,
                            onSeekFinished = onSeekFinished,
                            onSkipNext = onSkipNext,
                            onSkipPrevious = onSkipPrevious,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        CollapsedPlayer(
                            title = title,
                            imagePainter = imagePainter,
                            onPlayPauseClicked = onPlayPauseClicked,
                            isPlaying = isPlaying,
                            modifier = Modifier.fillMaxWidth()
                        )

                    }
                    onSheetStateChange(
                        scaffoldState.bottomSheetState.targetValue,
                        scaffoldState.bottomSheetState.calculateProgress()
                    )
                }
            }
        },
        sheetPeekHeight = sheetPeekHeight,
        scaffoldState = scaffoldState
    ) {
        behindContent(it)
    }

}

@Composable
private fun CollapsedPlayer(
    title: String,
    imagePainter: Painter,
    onPlayPauseClicked: () -> Unit,
    isPlaying: Boolean,
    modifier: Modifier = Modifier
) {

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .height(72.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CoverImage(
            imagePainter = imagePainter,
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(modifier = Modifier.weight(1f))
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

@Composable
private fun ExpandedPlayer(
    title: String,
    subtitle: String,
    imagePainter: Painter,
    onPlayPauseClicked: () -> Unit,
    isPlaying: Boolean,
    remainingTime: String,
    totalTime: String,
    progress: Float,
    onSeekValueChange: (Float) -> Unit,
    onSeekFinished: ((Float) -> Unit)? = null,
    onSkipNext: () -> Unit,
    onSkipPrevious: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CoverImage(
            imagePainter = imagePainter,
            modifier = Modifier
                .fillMaxWidth(.9f)
                .padding(top = 12.dp)
                .aspectRatio(1f)
                .clip(RoundedCornerShape(28.dp))
        )
        Spacer(modifier = Modifier.height(8.dp))
        SongInfo(
            title = title,
            subtitle = subtitle,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )

        PlayerControls(
            onPlayPauseClicked = onPlayPauseClicked,
            isPlaying = isPlaying,
            remainingTime = remainingTime,
            totalTime = totalTime,
            progress = progress,
            onSeekValueChange = onSeekValueChange,
            onSeekFinished = onSeekFinished,
            onSkipNext = onSkipNext,
            onSkipPrevious = onSkipPrevious,
            modifier = Modifier.padding(horizontal = 12.dp)
        )

    }
}

@Composable
private fun CoverImage(imagePainter: Painter, modifier: Modifier = Modifier) {
    Image(
        painter = imagePainter,
        contentDescription = "",
        contentScale = ContentScale.Crop,
        modifier = modifier
    )
}

@Composable
private fun PlayerControls(
    modifier: Modifier = Modifier,
    onSkipNext: () -> Unit = {},
    onSkipPrevious: () -> Unit = {},
    onPlayPauseClicked: () -> Unit,
    isPlaying: Boolean,
    remainingTime: String,
    totalTime: String,
    progress: Float,
    onSeekValueChange: (Float) -> Unit,
    onSeekFinished: ((Float) -> Unit)? = null,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Slider(
            value = progress, onValueChange = onSeekValueChange,
            onValueChangeFinished = {
                onSeekFinished?.invoke(progress)
            },
        )
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val seconds = remainingTime.substring(3..4)
            ElapsedTimeText(
                second = seconds,
                minuet = remainingTime.substring(0..1),
                style = MaterialTheme.typography.titleMedium
            )
            Text(text = totalTime, style = MaterialTheme.typography.titleMedium)
        }


        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val playImageVector = if (isPlaying)
                Icons.Rounded.Pause
            else Icons.Rounded.PlayArrow
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
                onCheckedChange = { _ ->
                    onPlayPauseClicked()
                },
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .size(60.dp)
            ) {
                Icon(
                    imageVector = playImageVector, contentDescription = "",
                    modifier = Modifier.size(36.dp)
                )
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

}

@Composable
private fun SongInfo(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
    ) {
        Text(
            text = title,
            modifier = Modifier.padding(bottom = 4.dp),
            style = MaterialTheme.typography.titleMedium,
            maxLines = 1
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1
        )

    }

}


@Composable
fun ElapsedTimeText(
    second: String,
    minuet: String,
    style: TextStyle = LocalTextStyle.current
) {
    Row {
        //first digit of minuet
        SlideUpAnimatedText(value = minuet[0].digitToInt(), textStyle = style)
        // second digit of minuet
        SlideUpAnimatedText(value = minuet[1].digitToInt(), textStyle = style)
        Text(text = ":", style = style)
        //first digit of second
        SlideUpAnimatedText(value = second[0].digitToInt(), textStyle = style)
        // second digit of second
        SlideUpAnimatedText(value = second[1].digitToInt(), textStyle = style)


    }
}

@OptIn(ExperimentalMaterialApi::class)
private fun BottomSheetState.calculateProgress(): Float {
    val currentState = currentValue
    return when {
        currentState == BottomSheetValue.Expanded && targetValue == BottomSheetValue.Expanded -> 1f
        currentState == BottomSheetValue.Collapsed && targetValue == BottomSheetValue.Collapsed -> 0f
        currentState == BottomSheetValue.Collapsed && targetValue == BottomSheetValue.Expanded -> progress
        else -> 1 - progress
    }

}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SlideUpAnimatedText(
    value: Int,
    textStyle: TextStyle = LocalTextStyle.current
) {
    AnimatedContent(targetState = value, label = "", transitionSpec = {
        if (targetState > initialState || targetState == 0) {
            (slideInVertically(animationSpec = spring()) { height -> height } + fadeIn(animationSpec = spring())).togetherWith(
                slideOutVertically { height -> -height } + fadeOut())
        } else {
            (slideInVertically(animationSpec = spring()) { height -> -height } + fadeIn(
                animationSpec = spring()
            )).togetherWith(
                slideOutVertically { height -> height } + fadeOut())
        }.using(
            SizeTransform(clip = false)
        )
    }) {
        Text(text = it.toString(), style = textStyle)
    }

}

@OptIn(ExperimentalMaterialApi::class)
@Preview
@Composable
fun PlayerScreenPreview() {
    PlayerScreen(
        title = "Music Title",
        imagePainter = painterResource(id = R.drawable.cover_preview),
        onPlayPauseClicked = { /*TODO*/ },
        isPlaying = false,
        totalTime = "04:32",
        remainingTime = "01:23",
        progress = .23f,
        subtitle = "Music Subtitle",
        onSeekValueChange = {},
        onSeekFinished = {},
        isSheetVisible = true,
        onSkipNext = {},
        onSkipPrevious = {},
        onSheetStateChange = { _, _ -> }
    ) {}

}
