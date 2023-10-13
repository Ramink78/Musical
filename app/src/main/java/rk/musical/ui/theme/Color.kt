package rk.musical.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.luminance
import kotlin.math.max
import kotlin.math.min

const val MinContrastOfPrimaryVsSurface = 3f

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)
val BackgroundColor = Color(0xFF0A0B0A)
val SurfaceColor = Color(0xFF141414)
val SurfaceVariantColor = Color(0xFF202020)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

fun Color.contrastAgainst(background: Color): Float {
    val fg = if (alpha < 1f) compositeOver(background) else this

    val fgLuminance = fg.luminance() + 0.05f
    val bgLuminance = background.luminance() + 0.05f

    return max(fgLuminance, bgLuminance) / min(fgLuminance, bgLuminance)
}
