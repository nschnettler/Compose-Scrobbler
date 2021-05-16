package de.schnettler.scrobbler.db

import ch.tutteli.atrium.api.fluent.en_GB.containsExactlyElementsOf
import ch.tutteli.atrium.api.fluent.en_GB.hasSize
import ch.tutteli.atrium.api.fluent.en_GB.notToBeNull
import ch.tutteli.atrium.api.fluent.en_GB.toBe
import ch.tutteli.atrium.api.verbs.expect
import de.schnettler.scrobbler.model.EntityWithStats
import de.schnettler.scrobbler.model.LastFmEntity
import de.schnettler.scrobbler.model.Stats
import de.schnettler.scrobbler.util.DataGenerator.generateAlbum
import de.schnettler.scrobbler.util.DataGenerator.generateAlbums
import de.schnettler.scrobbler.util.DataGenerator.generateAlbumsWithStats
import de.schnettler.scrobbler.util.DataGenerator.generateArtistWithStatsAndInfo
import de.schnettler.scrobbler.util.DatabaseTest
import de.schnettler.scrobbler.util.collectValue
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test

class ArtistDetailDaoTest : DatabaseTest() {

    @Test
    fun getTopAlbumsOfArtist_oneAlbum_returnsAlbum() = runBlockingTest {
        // GIVEN - Album and Stats in DB
        val artist = LastFmEntity.Artist("artist1", "url")
        val album = LastFmEntity.Album("album1", "url1", artist.name)
        val stats = Stats(album.id, 1, 2, 3)
        db.albumDao().insert(album)
        db.statDao().insert(stats)

        // When - loading AlbumWithStats
        val result = db.artistDetailDao().getTopAlbumsOfArtist(artist.name)

        // Then - Returns the right AlbumWithStats
        val expected = listOf(EntityWithStats.AlbumWithStats(album, stats))
        result.collectValue {
            expect(it).notToBeNull().containsExactlyElementsOf(expected)
        }
    }

    @Test
    fun getTopAlbumsOfArtist_returnsDescSortedList() = runBlockingTest {
        // GIVEN - Unsorted Albums and Stats in DB
        val artist = LastFmEntity.Artist("artist1", "url")
        val album = generateAlbums(3, artist.name)
        val stats = listOf(
            Stats(album[0].id, 10, 2, 3),
            Stats(album[1].id, 5, 2, 3),
            Stats(album[2].id, 30, 2, 3)
        )
        db.albumDao().insertAll(album)
        db.statDao().insertAll(stats)

        // When - Requesting Top Albums of Artist
        val result = db.artistDetailDao().getTopAlbumsOfArtist(artist.name)

        // Then - Returns the Top Albums, sorted by plays Desc
        val expected = listOf(
            EntityWithStats.AlbumWithStats(album[2], stats[2]),
            EntityWithStats.AlbumWithStats(album[0], stats[0]),
            EntityWithStats.AlbumWithStats(album[1], stats[1])
        )
        result.collectValue {
            expect(it).notToBeNull().containsExactlyElementsOf(expected)
        }
    }

    @Test
    fun getTopAlbumsOfArtist_returnsMax5Albums() = runBlockingTest {
        // GIVEN - More than 5 albums with stats in db
        val artist = LastFmEntity.Artist("artist1", "url")
        val albumWithStats = generateAlbumsWithStats(10, artist.name)
        db.albumDao().insertAll(albumWithStats.map { it.entity })
        db.statDao().insertAll(albumWithStats.map { it.stats })

        // When - Requesting Top Albums of Artist
        val result = db.artistDetailDao().getTopAlbumsOfArtist(artist.name)

        // Then - Returns the Top Albums, sorted by plays Desc
        result.collectValue {
            expect(it).notToBeNull().hasSize(5)
        }
    }

    @Test
    fun getTopAlbumsOfArtist_ignoreAlbumsWithoutStats() = runBlockingTest {
        // GIVEN - One album doesn't have stats
        val artist = LastFmEntity.Artist("artist1", "url")
        val albumWithoutStats = generateAlbum(10, artist.name)
        val albumWithStats = generateAlbumsWithStats(1, artist.name)
        db.albumDao().insert(albumWithoutStats)
        db.albumDao().insertAll(albumWithStats.map { it.entity })
        db.statDao().insertAll(albumWithStats.map { it.stats })

        // When - Requesting Top Albums of Artist
        val result = db.artistDetailDao().getTopAlbumsOfArtist(artist.name)

        // Then - Doesn't return the album w/ stats
        result.collectValue {
            expect(it).notToBeNull().hasSize(albumWithStats.size)
        }
    }

    @Test
    fun getArtistDetails_artistInfoStatsInDb_returnsArtistWithStatsAndInfo() = runBlockingTest {
        // GIVEN - Artist, Stats and Info in DB
        val artist = generateArtistWithStatsAndInfo(1).first()
        db.artistDao().insert(artist.artist)
        db.statDao().insert(artist.stats)
        db.infoDao().insert(artist.info)

        // WHEN - Requesting the artist details
        val returned = db.artistDetailDao().getArtistDetails(artist.artist.id)

        // THEN - Returned album should match inserted album
        returned.collectValue {
            expect(it).toBe(artist)
        }
    }

    @Test
    fun getArtistDetails_onlyArtistInDb_returnsAlbumWithStatsAndInfo() = runBlockingTest {
        // GIVEN - Only artist in DB, info and stats are missing
        val artist = generateArtistWithStatsAndInfo(1).first()
        db.artistDao().insert(artist.artist)

        // WHEN - Requesting artist details
        val returned = db.artistDetailDao().getArtistDetails(artist.artist.id)

        // THEN - Should return ArtistWithStatsAndInfo (where stats and info are null)
        returned.collectValue {
            expect(it).notToBeNull()
            expect(it?.artist).toBe(artist.artist)
            expect(it?.stats).toBe(null)
            expect(it?.info).toBe(null)
        }
    }
}