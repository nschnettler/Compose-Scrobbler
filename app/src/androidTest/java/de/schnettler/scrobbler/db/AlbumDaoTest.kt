package de.schnettler.scrobbler.db

import ch.tutteli.atrium.api.fluent.en_GB.toBe
import ch.tutteli.atrium.api.verbs.expect
import de.schnettler.scrobbler.model.LastFmEntity
import de.schnettler.scrobbler.util.DataGenerator.generateAlbums
import de.schnettler.scrobbler.util.DatabaseTest
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
}