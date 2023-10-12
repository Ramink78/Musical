package rk.musical.ui.component.draggablemenu

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.isSpecified

fun Offset.isSpecifiedAndValid(): Boolean {
    return isSpecified && isValid()
}