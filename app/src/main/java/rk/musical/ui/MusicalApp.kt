package rk.musical.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import rk.musical.R
import rk.musical.ui.screen.SongsScreen
import rk.musical.ui.theme.MusicalTheme

@Composable
fun MusicalApp() {
    Scaffold(
        bottomBar = {
            MusicalBottomNavigation()
        },
        content = { paddingValues ->
            SongsScreen(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(
                        start = 8.dp,
                        end = 8.dp,
                        top = 16.dp
                    ),
                onSongClick = {}
            )
        }
    )
}

@Composable
fun MusicalBottomNavigation(modifier: Modifier = Modifier) {
    NavigationBar(modifier = modifier) {
        NavigationBarItem(
            selected = true,
            onClick = {},
            icon = {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = stringResource(R.string.songs_tab_cd)
                )
            },
            label = {
                Text(text = stringResource(R.string.songs))
            }
        )

    }

}

@Preview
@Composable
fun MusicalBottomNavigationPreview() {
    MusicalTheme {
        MusicalApp()
    }

}