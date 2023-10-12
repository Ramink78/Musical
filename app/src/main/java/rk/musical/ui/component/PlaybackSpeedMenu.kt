package rk.musical.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import rk.musical.R
import rk.musical.ui.component.draggablemenu.DraggableMenu
import rk.musical.ui.component.draggablemenu.draggableMenuAnchor
import rk.musical.ui.component.draggablemenu.draggableMenuContainer
import rk.musical.ui.component.draggablemenu.rememberDraggableMenuState
import rk.musical.ui.theme.MusicalTheme
import rk.musical.ui.theme.Purple80

@Composable
fun PlaybackSpeedMenu(
    modifier: Modifier = Modifier,
    onItemSelected: (index: Int) -> Unit
) {
    var speedIconColor by remember {
        mutableStateOf(
            Color.White
        )
    }
    val state = rememberDraggableMenuState()
    val halfSpeed = PlaybackSpeed(Icons.Rounded.Speed, .5f, "0.5x")
    val normalSpeed = PlaybackSpeed(Icons.Rounded.Speed, 1f, "1.0x")
    val fastSpeed = PlaybackSpeed(Icons.Rounded.Speed, 1.5f, "1.5x")
    val fastestSpeed = PlaybackSpeed(Icons.Rounded.Speed, 2f, "2.0x")
    val menuItems = remember {
        mutableStateListOf(halfSpeed, normalSpeed, fastSpeed, fastestSpeed)
    }
    Box(
        modifier = modifier
            .draggableMenuContainer(state),
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.Center)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.playback_speed_icon),
                contentDescription = null,
                modifier = Modifier
                    .draggableMenuAnchor(state)
                    .size(32.dp),
                tint = speedIconColor
            )

            DraggableMenu(
                state = state, onItemSelected = {
                    speedIconColor = if (it == 1)
                        Color.White
                    else
                        Purple80
                    onItemSelected(it)
                },
                hoverBarBackgroundColor = MaterialTheme.colorScheme.primary

            ) {
                itemsIndexed(menuItems) { index, item ->
                    PlaybackSpeedMenuItem(
                        playbackSpeed = item,
                        isHovered = state.hoveredItemIndex == index
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun PlaybackSpeedMenuPreview() {
    MusicalTheme(darkTheme = true) {
        PlaybackSpeedMenu(onItemSelected = {})

    }
}

@Composable
fun PlaybackSpeedMenuItem(
    playbackSpeed: PlaybackSpeed,
    isHovered: Boolean = false
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .widthIn(min = 100.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {

        Text(
            text = playbackSpeed.title,
            color =
            if (isHovered)
                contentColorFor(backgroundColor = MaterialTheme.colorScheme.primary)
            else
                Color.Unspecified,
            style =
            if (isHovered)
                MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp)
            else
                MaterialTheme.typography.headlineSmall.copy(fontSize = 18.sp)

        )
    }

}

data class PlaybackSpeed(
    val icon: ImageVector,
    val speed: Float,
    val title: String
)