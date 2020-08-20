package de.schnettler.database.daos

import ch.tutteli.atrium.api.fluent.en_GB.containsExactlyElementsOf
import ch.tutteli.atrium.api.fluent.en_GB.hasSize
import ch.tutteli.atrium.api.fluent.en_GB.notToBeNull
import ch.tutteli.atrium.api.fluent.en_GB.toBe
import ch.tutteli.atrium.api.verbs.expect
import de.schnettler.database.DataGenerator.generateAlbum
import de.schnettler.database.DataGenerator.generateAlbumWithStatsAndInfo
import de.schnettler.database.DataGenerator.generateAlbums
import de.schnettler.database.DataGenerator.generateAlbumsWithStats
import de.schnettler.database.collectValue
import de.schnettler.database.models.EntityWithStats
import de.schnettler.database.models.LastFmEntity
import de.schnettler.database.models.Stats
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test

class AlbumDaoTest : DatabaseTest() {

    @Test
    fun getAlbumByName_parameterValid_returnsAlbum() = runBlockingTest {
        // GIVEN - Albums in Database
        val albums = listOf(
            LastFmEntity.Album("Album1", "url1", "artist1"),
            LastFmEntity.Album("Album2", "url2", "artist2")
        )
        db.albumDao().insertAll(albums)

        // WHEN - Get album by valid name
        val result = db.albumDao().getAlbumByName(albums.first().name, albums.first().artist)

        // THEN - Loaded the right album
        expect(result).toBe(albums.first())
    }

    @Test
    fun getAlbumByName_parameterInvalid_returnsNull() = runBlockingTest {
        // GIVEN - Albums in Database
        val albums = generateAlbums(2)
        db.albumDao().insertAll(albums)

        // WHEN - wrong artist name used
        val result = db.albumDao().getAlbumByName(albums.first().name, albums.last().artist)

        // THEN - Returns null
        expect(result).toBe(null)
    }

    @Test
    fun getTopAlbumsOfArtist_oneAlbum_returnsAlbum() = runBlockingTest {
        // GIVEN - Album and Stats in DB
        val artist = LastFmEntity.Artist("artist1", "url")
        val album = LastFmEntity.Album("album1", "url1", artist.name)
        val stats = Stats(album.id, 1, 2, 3)
        db.albumDao().insert(album)
        db.statDao().insert(stats)

        // When - loading AlbumWithStats
        val result = db.albumDao().getTopAlbumsOfArtist(artist.name)

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
        val result = db.albumDao().getTopAlbumsOfArtist(artist.name)

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
        val result = db.albumDao().getTopAlbumsOfArtist(artist.name)

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
        val result = db.albumDao().getTopAlbumsOfArtist(artist.name)

        // Then - Doesn't return the album w/ stats
        result.collectValue {
            expect(it).notToBeNull().hasSize(albumWithStats.size)
        }
    }

    @Test
    fun getAlbumWithStatsAndInfo_albumInfoStatsInDb_returnsAlbumWithStatsAndInfo() = runBlockingTest {
        // GIVEN - Album, Stats and Info in DB
        val artistName = "artist"
        val album = generateAlbumWithStatsAndInfo(1, artistName).first()
        db.albumDao().insert(album.entity)
        db.statDao().insert(album.stats)
        db.infoDao().insert(album.info)

        // WHEN - Requesting the album with info and stats
        val returned = db.albumDao().getAlbumWithStatsAndInfo(album.entity.id, artistName)

        // THEN - Returned album should match inserted album
        returned.collectValue {
            expect(it).toBe(album)
        }
    }

    @Test
    fun getAlbumWithStatsAndInfo_onlyAlbumInDb_returnsAlbumWithStatsAndInfo() = runBlockingTest {
        // GIVEN - Only album in DB, info and stats are missing
        val artistName = "artist"
        val album = generateAlbumWithStatsAndInfo(1, artistName).first()
        db.albumDao().insert(album.entity)

        // WHEN - Requesting the album with info and stats
        val returned = db.albumDao().getAlbumWithStatsAndInfo(album.entity.id, artistName)

        // THEN - Should return AlbumWithStatsAndInfo (where stats and info are null)
        returned.collectValue {
            expect(it).notToBeNull()
            expect(it?.entity).toBe(album.entity)
            expect(it?.stats).toBe(null)
            expect(it?.info).toBe(null)
        }
    }
}