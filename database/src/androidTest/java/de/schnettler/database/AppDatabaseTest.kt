package de.schnettler.database

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import androidx.room.Room
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.platform.app.InstrumentationRegistry
import ch.tutteli.atrium.api.fluent.en_GB.toBe
import ch.tutteli.atrium.api.verbs.expect
import de.schnettler.database.migration.MIGRATION_47_48
import de.schnettler.database.migration.MIGRATION_48_49
import de.schnettler.database.models.EntityInfo
import org.junit.Rule
import org.junit.Test
import java.io.IOException

class AppDatabaseTest {
    private val TEST_DB = "migration-test"

    private val converter = TypeConverter()

    private val ALL_MIGRATIONS = arrayOf(
        MIGRATION_47_48,
        MIGRATION_48_49
    )

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        AppDatabase::class.java.canonicalName,
        FrameworkSQLiteOpenHelperFactory()
    )

    @Test
    @Throws(IOException::class)
    fun migrateAll() {
        // GIVEN - Earliest Database (v47) with track
        val oldTrack = buildTrackToInsert_V47()
        val oldInfo = buildInfoToInsert_v47()
        helper.createDatabase(TEST_DB, 47).apply {
            insert("tracks", SQLiteDatabase.CONFLICT_REPLACE, oldTrack)
            insert("entity_info", SQLiteDatabase.CONFLICT_REPLACE, oldInfo)
            close()
        }

        // When db is migrated to latest version
        val db = Room.databaseBuilder(
            InstrumentationRegistry.getInstrumentation().targetContext,
            AppDatabase::class.java,
                TEST_DB
        ).addMigrations(*ALL_MIGRATIONS).build().apply {
            openHelper.writableDatabase
            close()
        }

        // THEN
        // 1. Track is correctly migrated
        val track = db.trackDao().getTrack(oldTrack.getAsString("id"), oldTrack.getAsString("artist"))
        expect(track?.name).toBe(oldTrack.getAsString("name"))
        expect(track?.url).toBe(oldTrack.getAsString("url"))
        expect(track?.album).toBe(oldTrack.getAsString("album"))
        expect(track?.albumId).toBe(oldTrack.getAsString("albumId"))
        expect(track?.imageUrl).toBe(oldTrack.getAsString("imageUrl"))

        // 2. Info is correctly migrated
        val info = db.infoDao().get(oldInfo.getAsString("id"))
        val expectedInfo = EntityInfo(
            id = oldInfo.getAsString("id"),
            tags = converter.stringToList(oldInfo.getAsString("tags")),
            duration = oldInfo.getAsLong("duration"),
            wiki = oldInfo.getAsString("wiki"),
            loved = false
        )
        expect(info).toBe(expectedInfo)
    }

    private fun buildTrackToInsert_V47(): ContentValues {
        val track = ContentValues()
        track.put("name", "Scream")
        track.put("url", "https://www.last.fm/music/Dreamcatcher/_/Scream")
        track.put("artist", "Dreamcatcher")
        track.put("album", "1st Album 'Dystopia : The Tree of Language")
        track.put("albumId", "album_dreamcatcher_dystopia")
        track.put("id", "track_scream_dreamcatcher")
        track.put("imageUrl", "screamImageUrl")
        return track
    }

    private fun buildInfoToInsert_v47(): ContentValues {
        return ContentValues().apply {
            put("id", "trackId")
            put("tags", "tag1, tag2")
            put("duration", 10)
            put("wiki", "LongWiki")
        }
    }
}