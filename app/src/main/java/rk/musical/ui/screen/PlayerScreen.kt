package rk.musical.ui.screen

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.areNavigationBarsVisible
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Repeat
import androidx.compose.material.icons.rounded.RepeatOne
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconToggleButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.Player
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.size.Size
import com.galaxygoldfish.waveslider.CircleThumb
import com.galaxygoldfish.waveslider.WaveSliderDefaults
import rk.musical.ui.component.WaveSlider
import rk.musical.ui.theme.MusicalTheme
import rk.musical.ui.theme.PurpleGrey40

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScreen(
    behindContent: @Composable (PaddingValues) -> Unit,
    sheetState: BottomSheetScaffoldState
) {
    val viewModel: PlayerScreenViewModel = hiltViewModel()
    val isVisibleState by viewModel.isVisibleSheetFlow.collectAsStateWithLifecycle()
    val sheetRadius by animateDpAsState(
        targetValue =
        if (sheetState.bottomSheetState.targetValue == SheetValue.Expanded) 0.dp else 16.dp,
        label = ""
    )
    PlayerScreen(
        sheetState = sheetState,
        isSheetVisible = isVisibleState,
        sheetRadius = sheetRadius,
        behindContent = behindContent
    )

}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun PlayerScreen(
    sheetState: BottomSheetScaffoldState,
    isSheetVisible: Boolean,
    sheetRadius: Dp = 16.dp,
    behindContent: @Composable (PaddingValues) -> Unit
) {
    val bottomNavigationHeight = 80.dp
    val collapsedHeight = if (WindowInsets.Companion.areNavigationBarsVisible) 90.dp else 60.dp
    val peekHeight = bottomNavigationHeight + collapsedHeight
    val sheetPeekHeight by animateDpAsState(
        targetValue =
        if (isSheetVisible) peekHeight else 0.dp, label = ""
    )
    BottomSheetScaffold(
        sheetDragHandle = null,
        sheetShape = RoundedCornerShape(topStart = sheetRadius, topEnd = sheetRadius),
        sheetContent = {
            if (isSheetVisible)
                Surface(modifier = Modifier.fillMaxSize()) {
                    Box {
                        Crossfade(
                            targetState = sheetState.bottomSheetState.targetValue,
                            label = "",
                        ) {
                            if (it == SheetValue.Expanded)
                                ExpandedPlayer(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .statusBarsPadding()
                                )
                            else CollapsedPlayer()
                        }
                    }
                }
        },
        sheetTonalElevation = 0.dp,
        sheetPeekHeight = sheetPeekHeight,
        scaffoldState = sheetState,
    ) {
        behindContent(it)
    }

}

@Composable
private fun CollapsedPlayer(
    modifier: Modifier = Modifier
) {
    val viewModel: CollapsedNowPlayingViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .height(64.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CoverImage(
            coverUri = uiState.playingSong.coverUri,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            modifier = Modifier.weight(1f),
            text = uiState.playingSong.title,
            style = MaterialTheme.typography.titleMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        IconButton(onClick = viewModel::togglePlay) {
            Icon(
                imageVector =
                if (uiState.isPlaying)
                    Icons.Rounded.Pause
                else
                    Icons.Rounded.PlayArrow,
                contentDescription = "",
                tint = MaterialTheme.colorScheme.primary
            )
        }

    }

}

@Composable
private fun ExpandedPlayer(
    modifier: Modifier = Modifier
) {
    val viewModel: ExpandedNowPlayingViewModel = hiltViewModel()
    val uiState by viewModel.nowPlayingUiStateFlow.collectAsStateWithLifecycle()
    val repeatMode by viewModel.repeatModeFlow.collectAsStateWithLifecycle()
    val isShuffleMode by viewModel.shuffleModeFlow.collectAsStateWithLifecycle()
    val progress by viewModel.uiProgress.collectAsStateWithLifecycle()
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CoverImage(
            coverUri = uiState.currentSong.coverUri,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .padding(horizontal = 8.dp)
                .clip(RoundedCornerShape(10.dp))
        )
        Spacer(modifier = Modifier.height(8.dp))
        SongInfo(
            title = uiState.currentSong.title,
            subtitle = uiState.currentSong.artist,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))

        PlayerControls(
            onPlayPauseClicked = viewModel::togglePlay,
            isPlaying = uiState.isPlaying,
            remainingTime = uiState.currentTime,
            totalTime = uiState.totalTime,
            progress = progress,
            repeatMode = repeatMode,
            isShuffleOn = isShuffleMode,
            onSeekValueChange = viewModel::updateProgress,
            onSeekFinished = viewModel::seekToProgress,
            onSkipNext = viewModel::skipToNext,
            onSkipPrevious = viewModel::skipToPrevious,
            modifier = Modifier.padding(horizontal = 12.dp),
            onRepeatClick = viewModel::changeRepeatMode,
            onShuffleClick = viewModel::toggleShuffleMode
        )


    }

}

@Composable
private fun CoverImage(coverUri: String?, modifier: Modifier = Modifier) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(coverUri)
            .size(Size.ORIGINAL)
            .crossfade(250)
            .build(),
        contentScale = ContentScale.Crop,
        modifier = modifier,
        contentDescription = ""
    )
}

@Composable
fun ShuffleModeButton(
    icon: ImageVector,
    enableColor: Color,
    disableColor: Color,
    onShuffleClick: () -> Unit,
    modifier: Modifier = Modifier,
    isEnable: Boolean = false,
    enableShape: Shape = CircleShape
) {
    val backgroundColor by animateColorAsState(
        targetValue =
        if (isEnable)
            enableColor
        else
            disableColor,
        label = "",
    )
    Box(
        modifier = modifier
            .size(42.dp)
            .clip(enableShape)
            .drawBehind {
                drawRect(backgroundColor)
            }
            .clickable(indication = null,
                interactionSource = remember { MutableInteractionSource() }) {
                onShuffleClick()

            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = "",
        )
    }

}

@SuppressLint("SwitchIntDef")
@Composable
fun RepeatModeButton(
    enableColor: Color,
    disableColor: Color,
    icon: ImageVector,
    onRepeatClick: () -> Unit,
    modifier: Modifier = Modifier,
    enableShape: Shape = CircleShape,
    @Player.RepeatMode repeatMode: Int = Player.REPEAT_MODE_OFF
) {
    val backgroundColor by animateColorAsState(
        targetValue =
        if (repeatMode == Player.REPEAT_MODE_OFF)
            disableColor
        else
            enableColor,
        label = "",

        )
    Box(
        modifier = modifier
            .size(42.dp)
            .clip(enableShape)
            .drawBehind {
                drawRect(backgroundColor)
            }
            .clickable(indication = null,
                interactionSource = remember { MutableInteractionSource() }) {
                onRepeatClick()

            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = "",
        )
    }

}

@Preview
@Composable
fun RepeatModeButtonPreview() {
    MusicalTheme(darkTheme = true) {
        RepeatModeButton(
            enableColor = MaterialTheme.colorScheme.primary,
            disableColor = Color.Transparent,
            icon = Icons.Rounded.RepeatOne,
            onRepeatClick = {},
            repeatMode = Player.REPEAT_MODE_ALL
        )
    }
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
    isShuffleOn: Boolean = false,
    onShuffleClick: () -> Unit,
    repeatMode: Int = 0,
    onRepeatClick: () -> Unit,
    onSeekValueChange: (Float) -> Unit,
    onSeekFinished: (Float) -> Unit
) {
    val nextIcon = remember {
        Icons.Rounded.SkipNext
    }
    val previousIcon = remember {
        Icons.Rounded.SkipPrevious
    }

    val shuffleIcon = remember() {
        Icons.Rounded.Shuffle
    }
    val repeatIcon = remember(key1 = repeatMode) {
        when (repeatMode) {
            0 -> Icons.Rounded.Repeat
            1 -> Icons.Rounded.RepeatOne
            else -> Icons.Rounded.Repeat
        }
    }
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val seconds = remainingTime.substring(3..4)
            ElapsedTimeText(
                second = seconds,
                minuet = remainingTime.substring(0..1),
                style = MaterialTheme.typography.labelMedium
            )
            WaveSlider(
                value = progress,
                onValueChange = onSeekValueChange,
                onValueChangeFinished = onSeekFinished,
                animationOptions = WaveSliderDefaults.animationOptions(
                    animateWave = isPlaying
                ),
                modifier = Modifier.weight(1f),
                thumb = { CircleThumb() },
            )
            Text(text = totalTime, style = MaterialTheme.typography.labelMedium)
        }
        Spacer(modifier = Modifier.height(16.dp))


        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            val playImageVector = if (isPlaying)
                Icons.Rounded.Pause
            else Icons.Rounded.PlayArrow
            Spacer(modifier = Modifier.weight(1f))
            RepeatModeButton(
                enableColor = PurpleGrey40,
                disableColor = Color.Transparent,
                icon = repeatIcon,
                repeatMode = repeatMode,
                onRepeatClick = onRepeatClick
            )
            Spacer(modifier = Modifier.weight(1f))
            IconButton(
                onClick = onSkipPrevious,
                modifier = Modifier
                    .size(40.dp)
            ) {
                Icon(
                    imageVector = previousIcon,
                    contentDescription = "",
                    modifier = Modifier.fillMaxSize()
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
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
                    imageVector = playImageVector,
                    contentDescription = "",
                    modifier = Modifier.size(36.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = onSkipNext,
                modifier = Modifier
                    .size(40.dp)
            ) {
                Icon(
                    imageVector = nextIcon,
                    contentDescription = "",
                    modifier = Modifier.fillMaxSize()

                )
            }
            Spacer(modifier = Modifier.weight(1f))
            ShuffleModeButton(
                icon = shuffleIcon,
                enableColor = PurpleGrey40,
                disableColor = Color.Transparent,
                onShuffleClick = onShuffleClick,
                isEnable = isShuffleOn,
            )
            Spacer(modifier = Modifier.weight(1f))

        }
    }

}

@Preview
@Composable
fun PlayerControlsPreview() {
    MusicalTheme(darkTheme = true) {
        PlayerControls(
            onPlayPauseClicked = { /*TODO*/ },
            isPlaying = true,
            remainingTime = "00:00",
            totalTime = "12:22",
            progress = .3f,
            onSeekValueChange = {},
            onSeekFinished = {},
            onRepeatClick = {},
            onShuffleClick = {}
        )
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
            style = MaterialTheme.typography.titleLarge,
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
