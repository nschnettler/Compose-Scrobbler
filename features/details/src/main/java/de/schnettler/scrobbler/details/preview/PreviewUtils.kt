package de.schnettler.scrobbler.details.preview

import de.schnettler.scrobbler.details.model.ArtistDetailEntity
import de.schnettler.scrobbler.model.EntityInfo
import de.schnettler.scrobbler.model.EntityWithStats
import de.schnettler.scrobbler.model.LastFmEntity
import de.schnettler.scrobbler.model.Stats

object PreviewUtils {
    fun generateFakeArtistInto() = ArtistDetailEntity(
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