package rk.musical.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import rk.musical.ui.theme.PurpleGrey80
import rk.musical.ui.theme.SurfaceVariantColor

@Composable
fun SongPlaceholder(
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        color = PurpleGrey80
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.MusicNote,
                contentDescription = "",
            )
        }
    }
}

@Composable
fun SongDetailPlaceholder(
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        color = SurfaceVariantColor
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.MusicNote,
                contentDescription = "",
                modifier = Modifier.size(42.dp)
            )
        }
    }
}
