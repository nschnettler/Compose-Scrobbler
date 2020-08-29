package de.schnettler.scrobbler.screens.details

import androidx.compose.foundation.Icon
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
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
import androidx.compose.material.ListItem
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import de.schnettler.database.models.EntityWithStats.TrackWithStats
import de.schnettler.database.models.EntityWithStatsAndInfo.ArtistWithStatsAndInfo
import de.schnettler.scrobbler.UIAction
import de.schnettler.scrobbler.UIAction.ListingSelected
import de.schnettler.scrobbler.components.ExpandingInfoCard
import de.schnettler.scrobbler.components.ListTitle
import de.schnettler.scrobbler.components.ListeningStats
import de.schnettler.scrobbler.components.ListingScroller
import de.schnettler.scrobbler.components.PlainListIconBackground
import de.schnettler.scrobbler.screens.TagCategory
import de.schnettler.scrobbler.util.InsetsAmbient
import de.schnettler.scrobbler.util.PlaysStyle
import de.schnettler.scrobbler.util.abbreviate
import de.schnettler.scrobbler.util.lerp
import de.schnettler.scrobbler.util.navigationBarsHeightPlus
import de.schnettler.scrobbler.util.offset
import de.schnettler.scrobbler.util.onSizeChanged
import de.schnettler.scrobbler.util.statusBarsHeight
import dev.chrisbanes.accompanist.coil.CoilImageWithCrossfade

@Composable
fun ArtistDetailScreen(
    artistInfo: ArtistWithStatsAndInfo,
    actionHandler: (UIAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Test(artistInfo = artistInfo, actionHandler = actionHandler)
//    ScrollableColumn(modifier = modifier) {
//        Backdrop(
//            imageUrl = artistInfo.entity.imageUrl,
//            modifier = Modifier.aspectRatio(16 / 10f),
//            placeholder = artistInfo.entity.name
//        )
//
//        Content(artistInfo = artistInfo, actionHandler = actionHandler)
//    }
}

@Composable fun Content(artistInfo: ArtistWithStatsAndInfo, actionHandler: (UIAction) -> Unit) {
    val (artist, stats, info) = artistInfo

    ExpandingInfoCard(info = info?.wiki)
    ListeningStats(item = stats)
    info?.tags?.let { TagCategory(tags = it, actionHandler = actionHandler) }
    ListTitle(title = "Top Tracks")
    TrackListWithStats(tracks = artistInfo.topTracks, actionHandler = actionHandler)

    ListingScroller(
        title = "Top Albums",
        content = artistInfo.topAlbums,
        height = 160.dp,
        playsStyle = PlaysStyle.PUBLIC_PLAYS,
        actionHandler = actionHandler
    )

    ListingScroller(
        title = "Ähnliche Künstler",
        content = artistInfo.similarArtists,
        height = 136.dp,
        playsStyle = PlaysStyle.NO_PLAYS,
        actionHandler = actionHandler
    )

    Spacer(modifier = Modifier.navigationBarsHeightPlus(8.dp))
}

@Composable fun ContentNew(artistInfo: ArtistWithStatsAndInfo, actionHandler: (UIAction) -> Unit,
                           onBackdropSizeChanged: (IntSize) -> Unit, scrollState: ScrollState,) {
    val (artist, stats, info) = artistInfo
    Column(Modifier.fillMaxWidth()) {
        Surface(
            modifier = Modifier.fillMaxWidth()
                .aspectRatio(16f / 10)
                .onSizeChanged(onBackdropSizeChanged)
        ) {
            artist.imageUrl?.let {
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

        Surface(
            modifier = Modifier.fillMaxWidth().wrapContentHeight(Alignment.Top),
            elevation = 2.dp
        ) {
            Column(Modifier.fillMaxWidth()) {
                ShowDetailsAppBar(
                    title = "Test",
                    elevation = 0.dp,
                    backgroundColor = Color.Transparent,
                )

                // OTHER CONTENT
                Content(artistInfo = artistInfo, actionHandler = actionHandler)
            }
        }
    }
}

@Composable
fun TrackListWithStats(tracks: List<TrackWithStats>, actionHandler: (UIAction) -> Unit) {
    tracks.forEachIndexed { index, (track, stats) ->
        ListItem(
            text = { Text(track.name) },
            secondaryText = {
                Text("${stats.listeners.abbreviate()} Hörer")
            },
            icon = { PlainListIconBackground { Text(text = "${index + 1}") } },
            modifier = Modifier.clickable(onClick = { actionHandler(ListingSelected(track)) })
        )
    }
}

@Composable
fun Test(artistInfo: ArtistWithStatsAndInfo, actionHandler: (UIAction) -> Unit) = ConstraintLayout(modifier = Modifier.fillMaxSize()) {
    val (appbar) = createRefs()

    val scrollState = rememberScrollState()
    var backdropHeight by remember { mutableStateOf(0) }
    
    ScrollableColumn(
        scrollState = scrollState,
        modifier = Modifier.fillMaxHeight()
    ) {
        ContentNew(
            artistInfo = artistInfo,
            actionHandler = actionHandler,
            onBackdropSizeChanged = { backdropHeight = it.height },
            scrollState = scrollState
        )
    }

    OverlaidStatusBarAppBar(
        scrollPosition = scrollState.value,
        backdropHeight = backdropHeight,
        appBar = {
            ShowDetailsAppBar(
                title = "TEST",
                backgroundColor = Color.Transparent,
                elevation = 0.dp,
            )
        },
        modifier = Modifier.fillMaxWidth()
            .constrainAs(appbar) {
                top.linkTo(parent.top)
            }
    )
}

@Composable
private fun OverlaidStatusBarAppBar(
    scrollPosition: Float,
    backdropHeight: Int,
    modifier: Modifier = Modifier,
    appBar: @Composable () -> Unit
) {

    val insets = InsetsAmbient.current
    val trigger = (backdropHeight - insets.systemBars.top).coerceAtLeast(0)

    val alpha = lerp(
        startValue = 0.5f,
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
private fun ShowDetailsAppBar(
    title: String,
    elevation: Dp,
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = { Text(text = title) },
        navigationIcon = {
            IconButton(onClick = {  }) {
                Icon(Icons.Default.ArrowBack)
            }
        },
        actions = {

        },
        elevation = elevation,
        backgroundColor = backgroundColor,
        modifier = modifier
    )
}