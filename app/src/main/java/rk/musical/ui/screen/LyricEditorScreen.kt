package rk.musical.ui.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import rk.musical.data.model.Lyric
import rk.musical.data.model.Song
import rk.musical.ui.theme.MusicalTheme
import rk.musical.ui.theme.persianTypography

@Composable
fun LyricEditorScreen(
    song: Song,
    lyric: Lyric?,
    onSubmit: (Lyric) -> Unit,
    modifier: Modifier = Modifier,
    backHandler: () -> Unit = {},
    onDismiss: () -> Unit
) {
    var lyricText by remember {
        mutableStateOf(lyric?.lyricText ?: "")
    }
    BackHandler {
        backHandler()
    }
    Surface(modifier = modifier) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .statusBarsPadding()
        ) {
            IconButton(onClick = onDismiss) {
                Icon(imageVector = Icons.Rounded.Close, contentDescription = "")
            }
            SongItem(song = song, onClick = {})
            Spacer(modifier = Modifier.height(8.dp))
            LyricCard(
                modifier =
                Modifier
                    .fillMaxWidth()
                    .height(400.dp),
                text = lyricText,
                onTextChanged = {
                    lyricText = it
                }
            )
            TextButton(
                onClick = {
                    if (lyric == null) {
                        onSubmit(
                            Lyric(
                                songId = song.id,
                                lyricText = lyricText
                            )
                        )
                    } else {
                        onSubmit(lyric.copy(lyricText = lyricText))
                    }
                },
                modifier = Modifier.align(Alignment.CenterHorizontally),
                enabled = lyricText.isNotEmpty()
            ) {
                Text(text = "Save")
            }
        }
    }
}

@Composable
fun LyricCard(
    modifier: Modifier = Modifier,
    text: String,
    onTextChanged: (String) -> Unit
) {
    Card(
        modifier = modifier
    ) {
        OutlinedTextField(
            value = text,
            onValueChange = onTextChanged,
            modifier = Modifier.fillMaxSize(),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor =
                Color.Transparent,
                focusedBorderColor = Color.Transparent
            ),
            textStyle = persianTypography.bodyMedium
        )
    }
}

@Preview
@Composable
fun LyricEditorScreenPreview() {
    val song = Song.Empty.copy(title = "Song Name", artist = "Artist name")
    val lyric = Lyric("", "This the song lyric")
    MusicalTheme(darkTheme = false) {
        LyricEditorScreen(
            song = song,
            lyric = lyric,
            onSubmit = {},
            onDismiss = {},
            modifier = Modifier.fillMaxSize()
        )
    }
}
