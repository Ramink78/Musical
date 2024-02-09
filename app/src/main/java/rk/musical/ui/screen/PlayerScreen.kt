@file:OptIn(ExperimentalMaterial3Api::class)

package rk.musical.ui.screen

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntOffsetAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Lyrics
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
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.Player
import androidx.media3.ui.DefaultTimeBar
import androidx.media3.ui.TimeBar
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import coil.size.Size
import kotlinx.coroutines.launch
import rk.musical.data.model.Song
import rk.musical.ui.component.LyricContent
import rk.musical.ui.component.PlaybackSpeedMenu
import rk.musical.ui.component.SongDetailPlaceholder
import rk.musical.ui.component.SongPlaceholder
import rk.musical.ui.theme.MusicalTheme
import rk.musical.utils.NowPlayingDynamicTheme
import rk.musical.utils.readableDuration
import rk.musical.utils.verticalGradientScrim

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScreen(
    behindContent: @Composable (PaddingValues) -> Unit,
    sheetPeakHeight: Dp = 0.dp,
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
        sheetPeakHeight = sheetPeakHeight,
        behindContent = behindContent
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun PlayerScreen(
    sheetState: BottomSheetScaffoldState,
    isSheetVisible: Boolean,
    sheetRadius: Dp = 16.dp,
    sheetPeakHeight: Dp,
    behindContent: @Composable (PaddingValues) -> Unit
) {
    val scope = rememberCoroutineScope()
    val sheetPeekHeightAnimate by animateDpAsState(
        targetValue =
        if (isSheetVisible) sheetPeakHeight else 0.dp,
        label = ""
    )
    BackHandler(
        enabled =
        sheetState.bottomSheetState.currentValue == SheetValue.Expanded
    ) {
        scope.launch {
            sheetState.bottomSheetState.partialExpand()
        }
    }
    BottomSheetScaffold(
        sheetDragHandle = null,
        sheetShape = RoundedCornerShape(topStart = sheetRadius, topEnd = sheetRadius),
        sheetContent = {
            if (isSheetVisible) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    Box {
                        Crossfade(
                            targetState = sheetState.bottomSheetState.targetValue,
                            label = ""
                        ) {
                            if (it == SheetValue.Expanded) {
                                ExpandedPlayer(
                                    modifier =
                                    Modifier
                                        .fillMaxSize()
                                )
                            } else {
                                CollapsedPlayer(
                                    Modifier.clickable {
                                        scope.launch {
                                            sheetState.bottomSheetState.expand()
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        },
        sheetTonalElevation = 0.dp,
        sheetPeekHeight = sheetPeekHeightAnimate,
        scaffoldState = sheetState
    ) {
        behindContent(it)
    }
}

@Composable
private fun CollapsedPlayer(modifier: Modifier = Modifier) {
    val viewModel: CollapsedNowPlayingViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Row(
        modifier =
        modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .height(64.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CoverImage(
            coverUri = uiState.playingSong.coverUri,
            modifier =
            Modifier
                .size(48.dp)
                .clip(CircleShape),
            size = Size(width = 128, 128),
            placeholder = { SongPlaceholder() }
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            modifier = Modifier.weight(1f),
            text = uiState.playingSong.title,
            style = MaterialTheme.typography.titleMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        IconButton(onClick = viewModel::togglePlay) {
            Icon(
                imageVector =
                if (uiState.isPlaying) {
                    Icons.Rounded.Pause
                } else {
                    Icons.Rounded.PlayArrow
                },
                contentDescription = "",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun LyricButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    IconButton(onClick = onClick, modifier = modifier) {
        Icon(
            imageVector = Icons.Rounded.Lyrics,
            tint = MaterialTheme.colorScheme.onSurface,
            contentDescription = ""
        )
    }
}

@Composable
private fun ExpandedPlayer(modifier: Modifier = Modifier) {
    val viewModel: ExpandedNowPlayingViewModel = hiltViewModel()
    val uiState = viewModel.nowPlayingUiStateFlow.collectAsStateWithLifecycle()
    val currentLyric by viewModel.currentLyric.collectAsStateWithLifecycle()
    val lyricUiState by viewModel.lyricUiState.collectAsStateWithLifecycle()
    val repeatMode by viewModel.repeatModeFlow.collectAsStateWithLifecycle()
    val isShuffleMode by viewModel.shuffleModeFlow.collectAsStateWithLifecycle()
    val coverBlurRadius by
    animateDpAsState(
        targetValue =
        if (lyricUiState.isVisibleLyric) {
            10.dp
        } else {
            0.dp
        },
        label = ""
    )
    val positionLambda: () -> Long = remember {
        { uiState.value.playbackPosition }
    }
    val skipToNext: () -> Unit = remember {
        { viewModel.skipToNext() }
    }
    val skipToPrevious: () -> Unit = remember {
        { viewModel.skipToPrevious() }
    }
    val togglePlay: () -> Unit = remember {
        { viewModel.togglePlay() }
    }
    val changeRepeatMode: () -> Unit = remember {
        { viewModel.changeRepeatMode() }
    }
    val toggleShuffleMode: () -> Unit = remember {
        { viewModel.toggleShuffleMode() }
    }
    val seekToProgress: (Long) -> Unit = remember {
        { viewModel.seekToProgress(it) }
    }
    val currentSongDuration: () -> Long = remember {
        { uiState.value.currentSong.duration }
    }
    val setPlaybackSpeed: (Int) -> Unit = remember {
        { viewModel.setPlaybackSpeed(it) }
    }
    val currentSong: () -> Song = remember {
        { uiState.value.currentSong }
    }
    val remainingTime: () -> String = remember {
        { uiState.value.currentTime }
    }

    NowPlayingDynamicTheme(coverUri = currentSong().coverUri ?: "") {
        Box {
            Column(
                modifier =
                modifier
                    .verticalGradientScrim(
                        color = MaterialTheme.colorScheme.background.copy(alpha = 0.50f),
                        startYPercentage = 1f,
                        endYPercentage = 0f
                    ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box {
                    CoverImage(
                        coverUri = currentSong().coverUri,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .padding(horizontal = 8.dp)
                            .statusBarsPadding()
                            .clip(RoundedCornerShape(10.dp))
                            .blur(coverBlurRadius),
                        placeholder = {
                            SongDetailPlaceholder()
                        }
                    )
                    if (lyricUiState.isVisibleLyric) {
                        LyricContent(
                            lyricText = currentLyric?.lyricText,
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f)
                                .padding(horizontal = 8.dp)
                                .statusBarsPadding()
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.surface.copy(alpha = .9f)),
                            onEditClick = {
                                viewModel.showEditorLyric()
                                viewModel.fetchLyric(currentSong().id)
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                SongInfo(
                    title = currentSong().title,
                    subtitle = currentSong().artist,
                    modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    onItemSelected = setPlaybackSpeed,
                    onLyricButtonClicked = {
                        if (lyricUiState.isVisibleLyric) {
                            viewModel.hideLyricCover()
                        } else {
                            viewModel.fetchLyric(currentSong().id)
                            viewModel.showLyricCover()
                        }
                    }
                )
                Spacer(modifier = Modifier.height(12.dp))

                PlayerControls(
                    onPlayPauseClicked = togglePlay,
                    isPlaying = uiState.value.isPlaying,
                    remainingTime = remainingTime,
                    totalTime = uiState.value.totalTime,
                    repeatMode = repeatMode,
                    isShuffleOn = isShuffleMode,
                    onSkipNext = skipToNext,
                    onSkipPrevious = skipToPrevious,
                    modifier = Modifier.padding(horizontal = 12.dp),
                    onRepeatClick = changeRepeatMode,
                    onShuffleClick = toggleShuffleMode,
                    onPositionChanged = seekToProgress,
                    currentPos = positionLambda,
                    duration = currentSongDuration
                )
            }
            AnimatedVisibility(
                visible = lyricUiState.isEditMode,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                LyricEditorScreen(
                    modifier = Modifier.fillMaxSize(),
                    backHandler = {
                        viewModel.hideEditorLyric()
                    },
                    onDismiss = { viewModel.hideEditorLyric() },
                    lyric = currentLyric,
                    song = currentSong(),
                    onSubmit = {
                        viewModel.submitLyric(it)
                        viewModel.hideEditorLyric()
                    }

                )
            }
        }
    }
}

@Composable
fun CoverImage(
    coverUri: String?,
    modifier: Modifier = Modifier,
    size: Size = Size.ORIGINAL,
    placeholder: @Composable () -> Unit
) {
    val context = LocalContext.current
    SubcomposeAsyncImage(
        model =
        ImageRequest.Builder(context)
            .data(coverUri)
            .size(size = size)
            .build(),
        contentScale = ContentScale.Crop,
        modifier = modifier,
        contentDescription = "",
        error = {
            placeholder()
        }
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
        if (isEnable) {
            enableColor
        } else {
            disableColor
        },
        label = ""
    )
    Box(
        modifier =
        modifier
            .size(42.dp)
            .clip(enableShape)
            .drawBehind {
                drawRect(backgroundColor)
            }
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                onShuffleClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = ""
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
        if (repeatMode == Player.REPEAT_MODE_OFF) {
            disableColor
        } else {
            enableColor
        },
        label = ""
    )
    Box(
        modifier =
        modifier
            .size(42.dp)
            .clip(enableShape)
            .drawBehind {
                drawRect(backgroundColor)
            }
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                onRepeatClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = ""
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
@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
private fun PlayerControls(
    modifier: Modifier = Modifier,
    onSkipNext: () -> Unit = {},
    onSkipPrevious: () -> Unit = {},
    onPlayPauseClicked: () -> Unit,
    isPlaying: Boolean,
    remainingTime: () -> String,
    totalTime: String,
    isShuffleOn: Boolean = false,
    onShuffleClick: () -> Unit,
    repeatMode: Int = 0,
    onRepeatClick: () -> Unit,
    currentPos: () -> Long = { 0L },
    duration: () -> Long = { 0L },
    onPositionChanged: (Long) -> Unit = {}
) {
    var isShowTimeTexts by remember {
        mutableStateOf(true)
    }

    val density = LocalDensity.current
    val sliderScale by
    animateFloatAsState(
        targetValue =
        if (isShowTimeTexts) 1f else 1.4f,
        label = ""
    )
    val remainingTimeOffset by
    animateIntOffsetAsState(
        targetValue =
        if (isShowTimeTexts) {
            IntOffset.Zero
        } else {
            with(density) {
                IntOffset(x = 100.dp.roundToPx(), y = 0)
            }
        },
        label = ""
    )
    val totalTimeOffset by
    animateIntOffsetAsState(
        targetValue =
        if (isShowTimeTexts) {
            IntOffset.Zero
        } else {
            with(density) {
                IntOffset(x = (-100).dp.roundToPx(), y = 0)
            }
        },
        label = ""
    )
    val sliderColor =
        MaterialTheme.colorScheme.primary.toArgb()
    val sliderBufferColor = Color.White.copy(.15f).toArgb()
    val sliderThumbColor = MaterialTheme.colorScheme.primary.toArgb()
    val sliderPosition = remember {
        mutableLongStateOf(0L)
    }

    val sliderListener = remember {
        object : TimeBar.OnScrubListener {
            override fun onScrubStart(timeBar: TimeBar, position: Long) {}
            override fun onScrubMove(timeBar: TimeBar, position: Long) {
                sliderPosition.longValue = position
                isShowTimeTexts = false
            }

            override fun onScrubStop(timeBar: TimeBar, position: Long, canceled: Boolean) {
                onPositionChanged(position)
                isShowTimeTexts = true
            }
        }
    }
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TimeIndicator(currentPos = { sliderPosition.longValue }, isVisible = !isShowTimeTexts)
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier =
            Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val seconds = remainingTime().substring(3..4)
            ElapsedTimeText(
                second = seconds,
                minuet = remainingTime().substring(0..1),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.offset {
                    totalTimeOffset
                }
            )
            AndroidView(
                factory = { context ->
                    DefaultTimeBar(context).apply {
                        addListener(sliderListener)
                        setPlayedColor(sliderColor)
                        setUnplayedColor(sliderBufferColor)
                        setScrubberColor(sliderThumbColor)
                    }
                },
                modifier =
                Modifier
                    .weight(1f)
                    .graphicsLayer {
                        scaleY = sliderScale
                        scaleX = sliderScale
                    },
                update = {
                    it.setDuration(duration())
                    it.setPosition(currentPos())
                },
                onRelease = {
                    it.removeListener(sliderListener)
                }
            )
            Text(
                text = totalTime,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.offset {
                    remainingTimeOffset
                }

            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            val playImageVector =
                if (isPlaying) {
                    Icons.Rounded.Pause
                } else {
                    Icons.Rounded.PlayArrow
                }
            Spacer(modifier = Modifier.weight(1f))
            RepeatModeButton(
                enableColor = MaterialTheme.colorScheme.surface,
                disableColor = Color.Transparent,
                icon = when (repeatMode) {
                    0 -> Icons.Rounded.Repeat
                    1 -> Icons.Rounded.RepeatOne
                    else -> Icons.Rounded.Repeat
                },
                repeatMode = repeatMode,
                onRepeatClick = onRepeatClick
            )
            Spacer(modifier = Modifier.weight(1f))
            IconButton(
                onClick = onSkipPrevious,
                modifier =
                Modifier
                    .size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.SkipPrevious,
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
                modifier =
                Modifier
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
                modifier =
                Modifier
                    .size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.SkipNext,
                    contentDescription = "",
                    modifier = Modifier.fillMaxSize()
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            ShuffleModeButton(
                icon = Icons.Rounded.Shuffle,
                enableColor = MaterialTheme.colorScheme.surface,
                disableColor = Color.Transparent,
                onShuffleClick = onShuffleClick,
                isEnable = isShuffleOn
            )
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun TimeIndicator(
    currentPos: () -> Long,
    modifier: Modifier = Modifier,
    isVisible: Boolean
) {
    val scaleAndAlpha by animateFloatAsState(
        targetValue =
        if (isVisible) 1f else 0f,
        label = ""
    )
    Box(
        modifier = modifier
            .graphicsLayer {
                scaleY = scaleAndAlpha
                scaleX = scaleAndAlpha
                alpha = scaleAndAlpha
            }
            .background(MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(50))
            .shadow(6.dp, shape = RoundedCornerShape(50))
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Text(
            text = readableDuration(currentPos()),
            style = MaterialTheme.typography.titleLarge,
            color = contentColorFor(
                Color.White.copy(.15f)
            )
        )
    }
}

@Preview
@Composable
fun PlayerControlsPreview() {
    MusicalTheme(darkTheme = true) {
        PlayerControls(
            onPlayPauseClicked = { /*TODO*/ },
            isPlaying = true,
            remainingTime = { "00:00" },
            totalTime = "12:22",
            onRepeatClick = {},
            onShuffleClick = {}
        )
    }
}

@Composable
private fun SongInfo(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    onItemSelected: (index: Int) -> Unit,
    onLyricButtonClicked: () -> Unit = {}
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = title,
            modifier = Modifier.padding(bottom = 4.dp),
            style = MaterialTheme.typography.titleLarge,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        Row {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelMedium,
                maxLines = 1,
                modifier = Modifier.weight(1f)
            )
            LyricButton(onClick = onLyricButtonClicked)
            PlaybackSpeedMenu(onItemSelected = onItemSelected)
        }
    }
}

@Composable
fun ElapsedTimeText(
    second: String,
    minuet: String,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current
) {
    Row(modifier = modifier) {
        // first digit of minuet
        SlideUpAnimatedText(value = minuet[0].digitToInt(), textStyle = style)
        // second digit of minuet
        SlideUpAnimatedText(value = minuet[1].digitToInt(), textStyle = style)
        Text(text = ":", style = style)
        // first digit of second
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
            (
                    slideInVertically(animationSpec = spring()) { height -> height } + fadeIn(
                        animationSpec = spring()
                    )
                    ).togetherWith(
                    slideOutVertically { height -> -height } + fadeOut()
                )
        } else {
            (
                    slideInVertically(animationSpec = spring()) { height -> -height } +
                            fadeIn(
                                animationSpec = spring()
                            )
                    ).togetherWith(
                    slideOutVertically { height -> height } + fadeOut()
                )
        }.using(
            SizeTransform(clip = false)
        )
    }) {
        Text(text = it.toString(), style = textStyle)
    }
}
