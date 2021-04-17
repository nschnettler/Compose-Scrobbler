package de.schnettler.scrobbler.core.util

import de.schnettler.scrobbler.core.model.EntityInfo
import de.schnettler.scrobbler.core.model.EntityType
import de.schnettler.scrobbler.core.model.EntityWithStats
import de.schnettler.scrobbler.core.model.EntityWithStatsAndInfo
import de.schnettler.scrobbler.core.model.LastFmEntity
import de.schnettler.scrobbler.core.model.ListType
import de.schnettler.scrobbler.core.model.Stats
import de.schnettler.scrobbler.core.model.TopListAlbum
import de.schnettler.scrobbler.core.model.TopListArtist
import de.schnettler.scrobbler.core.model.TopListEntry
import de.schnettler.scrobbler.core.model.TopListTrack
import de.schnettler.scrobbler.core.model.User

object PreviewUtils {

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