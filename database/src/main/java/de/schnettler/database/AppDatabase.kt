package de.schnettler.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import de.schnettler.database.daos.*
import de.schnettler.database.models.*

@Database(entities = [
    Session::class,
    Artist::class,
    Album::class,
    Track::class,
    TopListEntry::class,
    AuthToken::class,
    RelationEntity::class,
    User::class,
    LocalTrack::class
], version = 34)
@TypeConverters(TypeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun authDao(): AuthDao
    abstract fun chartDao(): ChartDao
    abstract fun artistDao(): ArtistDao
    abstract fun relationshipDao(): RelationshipDao
    abstract fun albumDao(): AlbumDao
    abstract fun trackDao(): TrackDao
    abstract fun userDao(): UserDao
    abstract fun localTrackDao(): LocalTrackDao
}

fun provideDatabase(context: Context) = Room.databaseBuilder(
    context,
    AppDatabase::class.java,
    "lastfm"
).fallbackToDestructiveMigration().build()