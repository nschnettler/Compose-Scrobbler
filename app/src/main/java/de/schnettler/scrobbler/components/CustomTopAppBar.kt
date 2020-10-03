package de.schnettler.scrobbler.components

import androidx.compose.foundation.ProvideTextStyle
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.preferredHeight
import androidx.compose.foundation.layout.preferredWidth
import androidx.compose.material.EmphasisAmbient
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideEmphasis
import androidx.compose.material.Surface
import androidx.compose.material.contentColorFor
import androidx.compose.material.primarySurface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private val TopAppBarElevation = 4.dp
private val AppBarHorizontalPadding = 4.dp
private val AppBarHeight = 56.dp
private val TitleInsetWithoutIcon = Modifier.preferredWidth(16.dp - AppBarHorizontalPadding)
private val TitleIconModifier = Modifier.fillMaxHeight().preferredWidth(72.dp - AppBarHorizontalPadding)

@Composable
fun CustomTopAppBar(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    backgroundColor: Color = MaterialTheme.colors.primarySurface,
    contentColor: Color = contentColorFor(backgroundColor),
    elevation: Dp = TopAppBarElevation
) {
    AppBar(backgroundColor, contentColor, elevation, RectangleShape, modifier) {
        val emphasisLevels = EmphasisAmbient.current
        if (navigationIcon == null) {
            Spacer(TitleInsetWithoutIcon)
        } else {
            Row(TitleIconModifier, verticalAlignment = Alignment.CenterVertically) {
                ProvideEmphasis(emphasisLevels.high, navigationIcon)
            }
        }

        Row(
            Modifier.fillMaxHeight().weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ProvideTextStyle(value = MaterialTheme.typography.h6) {
                ProvideEmphasis(emphasisLevels.high, title)
            }
        }

        ProvideEmphasis(emphasisLevels.medium) {
            Row(
                Modifier.fillMaxHeight(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
                children = actions
            )
        }
    }
}

@Composable
private fun AppBar(
    backgroundColor: Color,
    contentColor: Color,
    elevation: Dp,
    shape: Shape,
    modifier: Modifier = Modifier,
    children: @Composable RowScope.() -> Unit
) {
    Surface(
        color = backgroundColor,
        contentColor = contentColor,
        elevation = elevation,
        shape = shape,
    ) {
        Row(
            modifier.fillMaxWidth()
                .padding(start = AppBarHorizontalPadding, end = AppBarHorizontalPadding)
                .preferredHeight(AppBarHeight),
            horizontalArrangement = Arrangement.SpaceBetween,
            children = children
        )
    }
}