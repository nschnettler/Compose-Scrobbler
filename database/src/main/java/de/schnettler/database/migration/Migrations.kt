package de.schnettler.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_47_48 = object : Migration(47, 48) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE tracks ADD COLUMN loved INTEGER NOT NULL DEFAULT 0")
    }
}

@Suppress("MaxLineLength")
val MIGRATION_48_49 = object : Migration(48, 49) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE entity_info ADD COLUMN loved INTEGER NOT NULL DEFAULT 0")

        database.execSQL("CREATE TABLE tracks_new (name TEXT NOT NULL, url TEXT NOT NULL, artist TEXT NOT NULL, album TEXT, albumId TEXT, id TEXT  NOT NULL, imageUrl TEXT, PRIMARY KEY(id))")
        database.execSQL("INSERT INTO tracks_new (name, url, artist, album, albumId, id, imageUrl) SELECT name, url, artist, album, albumId, id, imageUrl FROM tracks")
        database.execSQL("DROP TABLE tracks")
        database.execSQL("ALTER TABLE tracks_new RENAME TO tracks")
    }
}