package de.schnettler.scrobbler.util

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.LayoutModifier
import androidx.compose.ui.Measurable
import androidx.compose.ui.MeasureScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.OnPositionedModifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntSize
import kotlin.math.roundToInt

fun Modifier.offset(getOffset: (IntSize) -> Offset) = then(OffsetModifier(getOffset))

private data class OffsetModifier(
    private val getOffset: (IntSize) -> Offset
) : LayoutModifier {
    override fun MeasureScope.measure(
        measurable: Measurable,
        constraints: Constraints
    ): MeasureScope.MeasureResult {
        val placeable = measurable.measure(constraints)
        return layout(placeable.width, placeable.height) {
            val offset = getOffset(IntSize(placeable.width, placeable.height))
            placeable.place(offset.x.roundToInt(), offset.y.roundToInt())
        }
    }
}

fun Modifier.onSizeChanged(
    onChange: (IntSize) -> Unit
) = composed {
    var lastSize by remember { mutableStateOf<IntSize?>(null) }

    object : OnPositionedModifier {
        override fun onPositioned(coordinates: LayoutCoordinates) {
            if (coordinates.size != lastSize) {
                lastSize = coordinates.size
                onChange(coordinates.size)
            }
        }
    }
}

fun lerp(
    startValue: Float,
    endValue: Float,
    fraction: Float
) = startValue + fraction * (endValue - startValue)