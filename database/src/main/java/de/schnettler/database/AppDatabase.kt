package de.schnettler.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import de.schnettler.database.daos.AuthDao
import de.schnettler.database.daos.ChartDao
import de.schnettler.database.daos.UserDao
import de.schnettler.database.models.Artist
import de.schnettler.database.models.AuthToken
import de.schnettler.database.models.ListEntry
import de.schnettler.database.models.Session

@Database(entities = [Session::class, Artist::class, ListEntry::class, AuthToken::class], version = 9)
abstract class AppDatabase : RoomDatabase() {
    abstract fun authDao(): AuthDao
    abstract fun chartDao(): ChartDao
    abstract fun userDao(): UserDao
}

fun provideDatabase(context: Context) = Room.databaseBuilder(
    context,
    AppDatabase::class.java,
    "lastfm"
).fallbackToDestructiveMigration().build()