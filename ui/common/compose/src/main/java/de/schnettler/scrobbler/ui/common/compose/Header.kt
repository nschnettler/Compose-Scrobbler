package de.schnettler.scrobbler.ui.common.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.preferredSize
import androidx.compose.material.AmbientContentAlpha
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.WithConstraints
import androidx.compose.ui.platform.AmbientDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun Header(
    title: String,
    modifier: Modifier = Modifier,
    loading: Boolean = false,
    action: (@Composable () -> Unit)? = null
) {
    Row(modifier) {
        Spacer(size = 16.dp, orientation = Orientation.Horizontal)

        Providers(AmbientContentAlpha provides ContentAlpha.high) {
            Text(
                text = title,
                style = MaterialTheme.typography.subtitle1,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        androidx.compose.foundation.layout.Spacer(modifier = Modifier.weight(1f, true))

        AnimatedVisibility(visible = loading) {
            AutoSizedCircularProgressIndicator(
                color = MaterialTheme.colors.secondary,
                modifier = Modifier.padding(8.dp).preferredSize(16.dp)
            )
        }

        if (action != null) action()

        Spacer(size = 16.dp, orientation = Orientation.Horizontal)
    }
}

// Default stroke size
private val DefaultStrokeWidth = 4.dp
// Preferred diameter for CircularProgressIndicator
private val DefaultDiameter = 40.dp
// Internal padding added by CircularProgressIndicator
private val InternalPadding = 4.dp

private val StrokeDiameterFraction = DefaultStrokeWidth / DefaultDiameter

@Composable
fun AutoSizedCircularProgressIndicator(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colors.primary
) {
    WithConstraints(modifier) {
        val diameter = with(AmbientDensity.current) {
            // We need to minus the padding added within CircularProgressIndicator
            min(constraints.maxWidth.toDp(), constraints.maxHeight.toDp()) - InternalPadding
        }

        CircularProgressIndicator(
            strokeWidth = (diameter * StrokeDiameterFraction).coerceAtLeast(1.dp),
            color = color
        )
    }
}
