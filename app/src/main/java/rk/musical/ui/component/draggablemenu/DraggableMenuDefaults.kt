package rk.musical.ui.component.draggablemenu

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp

object DraggableMenuDefaults {
    val Elevation = 12.dp

    val Offset = DpOffset(0.dp, -(32.dp))

    @Composable
    fun backgroundColor(): Color {
        return MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
    }

    @Composable
    fun hoverBarBackground(): Color {
        return MaterialTheme.colorScheme.primary
    }
}
