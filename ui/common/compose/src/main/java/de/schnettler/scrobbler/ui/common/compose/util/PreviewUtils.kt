package de.schnettler.scrobbler.ui.common.compose.util

import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import de.schnettler.database.models.EntityType
import de.schnettler.database.models.LastFmEntity
import de.schnettler.database.models.ListType
import de.schnettler.database.models.TopListAlbum
import de.schnettler.database.models.TopListArtist
import de.schnettler.database.models.TopListEntry
import de.schnettler.database.models.TopListTrack
import de.schnettler.database.models.User
import de.schnettler.scrobbler.ui.common.compose.theme.AppTheme

@Composable
fun ThemedPreview(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    AppTheme(darkTheme = darkTheme) {
        Surface {
            content()
        }
    }
}

object PreviewUtils {
    @Composable
    fun generateFakeUser() = User(
        "Nickname",
        10L,
        "",
        10,
        "Realname",
        10,
        "Germany",
        "",
        10
    )

    fun generateFakeArtistCharts(number: Int) = MutableList(number) { index ->
        TopListArtist(
            TopListEntry(
                "",
                EntityType.ARTIST,
                ListType.CHART,
                index,
                10
            ),
            LastFmEntity.Artist("Artist $index")
        )
    }

    fun generateFakeAlbumCharts(number: Int) = MutableList(number) { index ->
        TopListAlbum(
            TopListEntry(
                "",
                EntityType.ALBUM,
                ListType.CHART,
                index,
                10
            ),
            LastFmEntity.Album("Album $index", artist = "Artist $index")
        )
    }

    fun generateFakeTrackCharts(number: Int) = MutableList(number) { index ->
        TopListTrack(
            TopListEntry(
                "",
                EntityType.TRACK,
                ListType.CHART,
                index,
                10
            ),
            LastFmEntity.Track("Track $index", artist = "Artist $index")
        )
    }
}