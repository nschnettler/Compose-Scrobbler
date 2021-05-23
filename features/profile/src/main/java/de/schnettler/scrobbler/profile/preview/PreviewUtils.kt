package de.schnettler.scrobbler.profile.preview

import de.schnettler.scrobbler.model.EntityType
import de.schnettler.scrobbler.model.LastFmEntity
import de.schnettler.scrobbler.model.ListType
import de.schnettler.scrobbler.model.TopListAlbum
import de.schnettler.scrobbler.model.TopListArtist
import de.schnettler.scrobbler.model.TopListEntry
import de.schnettler.scrobbler.model.TopListTrack
import de.schnettler.scrobbler.model.User

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
                ListType.USER,
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