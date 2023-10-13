package rk.musical.ui.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Slider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.lerp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.galaxygoldfish.waveslider.LocalThumbColor
import com.galaxygoldfish.waveslider.PillThumb
import com.galaxygoldfish.waveslider.WaveAnimationOptions
import com.galaxygoldfish.waveslider.WaveOptions
import com.galaxygoldfish.waveslider.WaveSliderColors
import com.galaxygoldfish.waveslider.WaveSliderDefaults
import kotlin.math.sin

private fun stepsToTickFractions(steps: FloatArray): FloatArray {
    return if (steps.isEmpty()) {
        floatArrayOf()
    } else {
        FloatArray(steps.size + 2) { it.toFloat() / (steps.size + 1) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WaveSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    onValueChangeFinished: (Float) -> Unit = {},
    colors: WaveSliderColors = WaveSliderDefaults.colors(),
    animationOptions: WaveAnimationOptions = WaveSliderDefaults.animationOptions(),
    waveOptions: WaveOptions = WaveSliderDefaults.waveOptions(),
    enabled: Boolean = true,
    thumb: @Composable () -> Unit = { PillThumb() },
    steps: Int = 0
) {
    val amplitude = waveOptions.amplitude
    val frequency = waveOptions.frequency

    var isDragging by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            when (interaction) {
                is DragInteraction.Start -> {
                    isDragging = true
                }

                is DragInteraction.Stop -> {
                    isDragging = false
                }
            }
        }
    }
    val infiniteTransition = rememberInfiniteTransition(label = "Wave infinite transition")
    val phaseShiftFloat =
        infiniteTransition.animateFloat(
            label = "Wave phase shift",
            initialValue = 0F,
            targetValue = 90f,
            animationSpec =
            infiniteRepeatable(
                animation =
                keyframes {
                    durationMillis = 1000
                },
                repeatMode = RepeatMode.Restart
            )
        ).value
    Slider(
        steps = steps,
        value = value,
        onValueChangeFinished = {
            onValueChangeFinished(value)
        },
        onValueChange = onValueChange,
        interactionSource = interactionSource,
        enabled = enabled,
        modifier = modifier,
        thumb = {
            CompositionLocalProvider(
                LocalThumbColor provides
                    animateColorAsState(
                        targetValue =
                        if (enabled) {
                            colors.thumbColor
                        } else {
                            colors.disabledThumbColor
                        },
                        label = "Thumb color"
                    ).value
            ) {
                thumb()
            }
        },
        track = { sliderState ->
            val animatedAmplitude =
                animateFloatAsState(
                    targetValue =
                    if (animationOptions.flatlineOnDrag) {
                        if (animationOptions.reverseFlatline) {
                            if (isDragging) amplitude else 0F
                        } else {
                            if (isDragging) 0F else amplitude
                        }
                    } else {
                        amplitude
                    },
                    label = "Wave amplitude"
                ).value
            Canvas(modifier = Modifier.fillMaxWidth()) {
                val centerY = size.height / 2f
                val startX = 0F
                val endX = size.width * value
                val path = Path()
                for (x in startX.toInt()..endX.toInt()) {
                    var modifiedX = x.toFloat()
                    if (animationOptions.animateWave && enabled) {
                        if (animationOptions.reverseDirection) {
                            modifiedX += phaseShiftFloat
                        } else {
                            modifiedX -= phaseShiftFloat
                        }
                    }
                    val y = (animatedAmplitude * sin(frequency * modifiedX))
                    path.moveTo(x.toFloat(), centerY - y)
                    path.lineTo(x.toFloat(), centerY - y)
                }
                drawPath(
                    path = path,
                    color =
                    if (enabled) {
                        colors.activeTrackColor
                    } else {
                        colors.disabledActiveTrackColor
                    },
                    style = Stroke(width = 5.dp.toPx(), cap = StrokeCap.Round)
                )
                drawLine(
                    color =
                    if (enabled) {
                        colors.inactiveTrackColor
                    } else {
                        colors.disabledInactiveTrackColor
                    },
                    strokeWidth = 5.dp.toPx(),
                    cap = StrokeCap.Round,
                    start = Offset(endX + 1, centerY),
                    end = Offset(size.width, centerY)
                )
                stepsToTickFractions(sliderState.tickFractions).groupBy {
                    it > sliderState.activeRange.endInclusive ||
                        it < sliderState.activeRange.start
                }.forEach { (outsideFraction, list) ->
                    drawPoints(
                        points =
                        list.map {
                            Offset(
                                x =
                                lerp(
                                    start = Offset(startX, centerY),
                                    stop = Offset(size.width, centerY),
                                    fraction = it
                                ).x,
                                y = center.y
                            )
                        },
                        pointMode = PointMode.Points,
                        color =
                        if (outsideFraction) {
                            if (enabled) {
                                colors.inactiveTickColor
                            } else {
                                colors.disabledInactiveTickColor
                            }
                        } else {
                            if (animatedAmplitude == 0F) {
                                if (enabled) {
                                    colors.activeTickColor
                                } else {
                                    colors.disabledActiveTickColor
                                }
                            } else {
                                Color.Transparent
                            }
                        },
                        strokeWidth = 10F,
                        cap = StrokeCap.Round
                    )
                }
            }
        }
    )
}
