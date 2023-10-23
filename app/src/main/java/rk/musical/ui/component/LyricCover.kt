package rk.musical.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ModeEdit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import rk.musical.R
import rk.musical.ui.theme.persianTypography

@Composable
fun LyricContent(
    modifier: Modifier = Modifier,
    lyricText: String? = null,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    onEditClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        Text(
            modifier = Modifier
                .padding(8.dp)
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            textAlign = TextAlign.Justify,
            text = lyricText ?: stringResource(id = R.string.empty_lyric),
            style = persianTypography.bodyMedium,
            color = textColor
        )
        IconButton(onClick = onEditClick, modifier = Modifier.align(Alignment.Start)) {
            Icon(imageVector = Icons.Rounded.ModeEdit, contentDescription = "")
        }
    }
}

@Preview
@Composable
fun LyricContentPreview() {
    LyricContent(
        lyricText = "This is a sample lyric text.",
        onEditClick = {}
    )
}
