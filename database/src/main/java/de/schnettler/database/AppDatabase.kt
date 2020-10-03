package de.schnettler.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import de.schnettler.database.daos.AlbumDao
import de.schnettler.database.daos.ArtistDao
import de.schnettler.database.daos.ArtistRelationDao
import de.schnettler.database.daos.AuthDao
import de.schnettler.database.daos.ChartDao
import de.schnettler.database.daos.EntityInfoDao
import de.schnettler.database.daos.LocalTrackDao
import de.schnettler.database.daos.SessionDao
import de.schnettler.database.daos.StatsDao
import de.schnettler.database.daos.TrackDao
import de.schnettler.database.daos.UserDao
import de.schnettler.database.models.AuthToken
import de.schnettler.database.models.EntityInfo
import de.schnettler.database.models.LastFmEntity
import de.schnettler.database.models.RelatedArtistEntry
import de.schnettler.database.models.Scrobble
import de.schnettler.database.models.Session
import de.schnettler.database.models.Stats
import de.schnettler.database.models.TopListEntry
import de.schnettler.database.models.User
import dev.matrix.roomigrant.GenerateRoomMigrations

@Database(
    entities = [
        Session::class,
        LastFmEntity.Artist::class,
        LastFmEntity.Album::class,
        LastFmEntity.Track::class,
        TopListEntry::class,
        AuthToken::class,
        User::class,
        Scrobble::class,
        RelatedArtistEntry::class,
        Stats::class,
        EntityInfo::class
    ], version = 53
)
@Suppress("TooManyFunctions")
@TypeConverters(TypeConverter::class)
@GenerateRoomMigrations
abstract class AppDatabase : RoomDatabase() {
    abstract fun authDao(): AuthDao
    abstract fun chartDao(): ChartDao
    abstract fun artistDao(): ArtistDao
    abstract fun albumDao(): AlbumDao
    abstract fun trackDao(): TrackDao
    abstract fun userDao(): UserDao
    abstract fun localTrackDao(): LocalTrackDao
    abstract fun statDao(): StatsDao
    abstract fun infoDao(): EntityInfoDao
    abstract fun relationDao(): ArtistRelationDao
    abstract fun sessionDao(): SessionDao
}

@Suppress("SpreadOperator")
fun provideDatabase(context: Context) = Room
    .databaseBuilder(context, AppDatabase::class.java, "lastfm")
    .addMigrations(*AppDatabase_Migrations.build())
    .build()