package rk.musical.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import rk.musical.ui.theme.persianTypography

@Composable
fun LyricContent(
    modifier: Modifier = Modifier,
    lyricText: String = ""
) {
    Box(modifier = modifier) {
        Text(
            text = lyricText,
            style = persianTypography.bodyMedium
        )
    }
}

@Preview
@Composable
fun LyricContentPreview() {
    LyricContent(
        lyricText = "This is a sample lyric text."
    )
}
