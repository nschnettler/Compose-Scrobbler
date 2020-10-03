package de.schnettler.scrobbler.components

import androidx.compose.foundation.Icon
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ConstraintLayout
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import de.schnettler.scrobbler.UIAction
import de.schnettler.scrobbler.util.InsetsAmbient
import de.schnettler.scrobbler.util.MenuAction
import de.schnettler.scrobbler.util.lerp
import de.schnettler.scrobbler.util.offset
import de.schnettler.scrobbler.util.onSizeChanged
import de.schnettler.scrobbler.util.statusBarsHeight
import dev.chrisbanes.accompanist.coil.CoilImageWithCrossfade

@Composable
fun CollapsingToolbar(
    imageUrl: String?,
    title: String,
    statusBarGuardAlpha: Float = 0.5F,
    actionHandler: (UIAction) -> Unit = {},
    menuActions: List<MenuAction> = emptyList(),
    content: @Composable () -> Unit,
) = ConstraintLayout(modifier = Modifier.fillMaxSize()) {
    val (appbar) = createRefs()
    val scrollState = rememberScrollState()
    var backdropHeight by remember { mutableStateOf(0) }

    ScrollableColumn(
        scrollState = scrollState,
        modifier = Modifier.fillMaxHeight()
    ) {
        Content(
            onBackdropSizeChanged = { backdropHeight = it.height },
            scrollState = scrollState,
            realContent = content,
            imageUrl = imageUrl,
            title = title,
            actionHandler = actionHandler,
            menuActions = menuActions
        )
    }

    OverlaidStatusBarAppBar(
        scrollPosition = scrollState.value,
        backdropHeight = backdropHeight,
        appBar = {
            Toolbar(
                title = title,
                elevation = 0.dp,
                backgroundColor = Color.Transparent,
                actionHandler = actionHandler,
                menuActions = menuActions
            )
        },
        modifier = Modifier.fillMaxWidth().constrainAs(appbar) { top.linkTo(parent.top) },
        statusBarGuardAlpha = statusBarGuardAlpha
    )
}

@Composable
private fun Toolbar(
    title: String,
    elevation: Dp,
    backgroundColor: Color,
    modifier: Modifier = Modifier,
    actionHandler: (UIAction) -> Unit = {},
    menuActions: List<MenuAction> = emptyList()
) {
    TopAppBar(
        title = { Text(text = title, maxLines = 1, overflow = TextOverflow.Ellipsis) },
        navigationIcon = {
            IconButton(onClick = { actionHandler(UIAction.NavigateUp) }) {
                Icon(Icons.Default.ArrowBack)
            }
        },
        elevation = elevation,
        backgroundColor = backgroundColor,
        modifier = modifier,
        actions = {
            menuActions.forEach { menuItem ->
                IconButton(onClick = {
                    menuItem.action?.let {
                        actionHandler(it)
                    }
                }) {
                    Icon(menuItem.icon)
                }
            }
        }
    )
}

@Composable
private fun OverlaidStatusBarAppBar(
    scrollPosition: Float,
    backdropHeight: Int,
    modifier: Modifier = Modifier,
    statusBarGuardAlpha: Float,
    appBar: @Composable () -> Unit
) {

    val insets = InsetsAmbient.current
    val trigger = (backdropHeight - insets.systemBars.top).coerceAtLeast(0)

    val alpha = lerp(
        startValue = statusBarGuardAlpha,
        endValue = 1f,
        fraction = if (trigger > 0) (scrollPosition / trigger).coerceIn(0f, 1f) else 0f
    )

    Surface(
        color = MaterialTheme.colors.surface.copy(alpha = alpha),
        elevation = if (scrollPosition >= trigger) 2.dp else 0.dp,
        modifier = modifier
    ) {
        Column(Modifier.fillMaxWidth()) {
            Spacer(Modifier.statusBarsHeight())
            if (scrollPosition >= trigger) {
                appBar()
            }
        }
    }
}

@Composable
private fun Content(
    onBackdropSizeChanged: (IntSize) -> Unit,
    scrollState: ScrollState,
    imageUrl: String?,
    title: String,
    actionHandler: (UIAction) -> Unit = {},
    menuActions: List<MenuAction> = emptyList(),
    realContent: @Composable () -> Unit
) {
    Column(Modifier.fillMaxWidth()) {
        Surface(
            modifier = Modifier.fillMaxWidth()
                .aspectRatio(16f / 10)
                .onSizeChanged(onBackdropSizeChanged)
        ) {
            imageUrl?.let {
                CoilImageWithCrossfade(
                    data = it,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize().offset { size ->
                        Offset(
                            x = 0f,
                            y = (scrollState.value / 2)
                                .coerceIn(-size.height.toFloat(), size.height.toFloat())
                        )
                    }
                )
            }
        }
    }

    Surface(
        modifier = Modifier.fillMaxWidth().wrapContentHeight(Alignment.Top),
        elevation = 0.dp
    ) {
        Column(Modifier.fillMaxWidth()) {
            Toolbar(
                title = title,
                elevation = 0.dp,
                backgroundColor = Color.Transparent,
                actionHandler = actionHandler,
                menuActions = menuActions
            )
            realContent()
        }
    }
}