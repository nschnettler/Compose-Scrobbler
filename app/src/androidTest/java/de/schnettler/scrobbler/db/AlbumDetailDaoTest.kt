package de.schnettler.scrobbler.db

import ch.tutteli.atrium.api.fluent.en_GB.notToBeNull
import ch.tutteli.atrium.api.fluent.en_GB.toBe
import ch.tutteli.atrium.api.verbs.expect
import de.schnettler.scrobbler.util.DataGenerator.generateAlbumWithStatsAndInfo
import de.schnettler.scrobbler.util.DatabaseTest
import de.schnettler.scrobbler.util.collectValue
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test

class AlbumDetailDaoTest : DatabaseTest() {
    @Test
    fun getAlbumWithStatsAndInfo_albumInfoStatsInDb_returnsAlbumWithStatsAndInfo() = runBlockingTest {
        // GIVEN - Album, Stats and Info in DB
        val artistName = "artist"
        val album = generateAlbumWithStatsAndInfo(1, artistName).first()
        db.albumDao().insert(album.album)
        db.statDao().insert(album.stats)
        db.infoDao().insert(album.info)
        db.artistDao().insert(album.artist)

        // WHEN - Requesting the album with info and stats
        val returned = db.albumDetailDao().getAlbumWithStatsAndInfo(album.album.id, artistName)

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
        db.albumDao().insert(album.album)

        // WHEN - Requesting the album with info and stats
        val returned = db.albumDetailDao().getAlbumWithStatsAndInfo(album.album.id, artistName)

        // THEN - Should return AlbumWithStatsAndInfo (where stats and info are null)
        returned.collectValue {
            expect(it).notToBeNull()
            expect(it?.album).toBe(album.album)
            expect(it?.stats).toBe(null)
            expect(it?.info).toBe(null)
        }
    }
}