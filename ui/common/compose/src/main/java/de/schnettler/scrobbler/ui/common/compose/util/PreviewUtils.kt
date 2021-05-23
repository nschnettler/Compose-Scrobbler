package de.schnettler.scrobbler.ui.common.compose.util

import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import de.schnettler.database.models.EntityInfo
import de.schnettler.database.models.EntityType
import de.schnettler.database.models.EntityWithStats
import de.schnettler.database.models.EntityWithStatsAndInfo
import de.schnettler.database.models.LastFmEntity
import de.schnettler.database.models.ListType
import de.schnettler.database.models.Stats
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

    fun generateFakeArtistInto() = EntityWithStatsAndInfo.ArtistWithStatsAndInfo(
        LastFmEntity.Artist("Artist name"),
        stats = Stats("", 10, 20, 30),
        EntityInfo("", listOf("tag 1", "tag 2"), wiki = "Artist description & bio")
    ).apply {
        topTracks = generateFakeTracksWithStats(1)
        topAlbums = generateFakeAlbumsWithStats(3)
    }

    fun generateFakeTracksWithStats(number: Int) = MutableList(number) { index ->
        EntityWithStats.TrackWithStats(
            LastFmEntity.Track(
                "Track $index",
                artist = "artist"
            ),
            Stats("", 10, 20, 30)
        )
    }

    fun generateFakeAlbumsWithStats(number: Int) = MutableList(number) { index ->
        EntityWithStats.AlbumWithStats(
            LastFmEntity.Album(
                "Album $index",
                artist = "artist"
            ),
            Stats("", 10, 20, 30)
        )
    }
}