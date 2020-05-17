package de.schnettler.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import de.schnettler.database.daos.AuthDao
import de.schnettler.database.daos.TopListDao
import de.schnettler.database.models.Artist
import de.schnettler.database.models.ListEntry
import de.schnettler.database.models.Session

@Database(entities = [Session::class, Artist::class, ListEntry::class], version = 7)
abstract class AppDatabase : RoomDatabase() {
    abstract fun authDao(): AuthDao
    abstract fun topListDao(): TopListDao
}

fun provideDatabase(context: Context) = Room.databaseBuilder(
    context,
    AppDatabase::class.java,
    "lastfm"
).fallbackToDestructiveMigration().build()