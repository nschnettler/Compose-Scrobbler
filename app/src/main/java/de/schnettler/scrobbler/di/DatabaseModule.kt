package de.schnettler.scrobbler.di

import android.app.Application
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.schnettler.database.daos.AuthDao
import de.schnettler.database.daos.ChartDao
import de.schnettler.database.daos.ImageDao
import de.schnettler.database.daos.SessionDao
import de.schnettler.database.daos.UserDao
import de.schnettler.scrobbler.AppDatabase
import de.schnettler.scrobbler.details.db.AlbumDetailDao
import de.schnettler.scrobbler.details.db.ArtistDetailDao
import de.schnettler.scrobbler.details.db.ArtistRelationDao
import de.schnettler.scrobbler.details.db.EntityInfoDao
import de.schnettler.scrobbler.details.db.StatsDao
import de.schnettler.scrobbler.details.db.TrackDetailDao
import de.schnettler.scrobbler.getDatabase
import de.schnettler.scrobbler.history.domain.HistoryDao
import de.schnettler.scrobbler.persistence.dao.AlbumDao
import de.schnettler.scrobbler.persistence.dao.ArtistDao
import de.schnettler.scrobbler.persistence.dao.TrackDao
import de.schnettler.scrobbler.submission.domain.SubmissionDao
import javax.inject.Singleton

@Suppress("TooManyFunctions")
@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(application: Application) = getDatabase(application)

    @Provides
    @Singleton
    fun provideUserDao(database: AppDatabase): UserDao = database.userDao()

    @Provides
    @Singleton
    fun provideArtistDao(database: AppDatabase): ArtistDao = database.artistDao()

    @Provides
    @Singleton
    fun provideAlbumDao(database: AppDatabase): AlbumDao = database.albumDao()

    @Provides
    @Singleton
    fun provideTrackDao(database: AppDatabase): TrackDao = database.trackDao()

    @Provides
    @Singleton
    fun provideChartDao(database: AppDatabase): ChartDao = database.chartDao()

    @Provides
    @Singleton
    fun provideAuthDao(database: AppDatabase): AuthDao = database.authDao()

    @Provides
    @Singleton
    fun provideSubmissionDao(database: AppDatabase): SubmissionDao = database.submissionDao()

    @Provides
    @Singleton
    fun provideHistoryDao(database: AppDatabase): HistoryDao = database.historyDao()

    @Provides
    @Singleton
    fun provideStatsDao(database: AppDatabase): StatsDao = database.statDao()

    @Provides
    @Singleton
    fun provideInfoDao(database: AppDatabase): EntityInfoDao = database.infoDao()

    @Provides
    @Singleton
    fun provideRelationDao(database: AppDatabase): ArtistRelationDao = database.relationDao()

    @Provides
    @Singleton
    fun provideSessionDao(database: AppDatabase): SessionDao = database.sessionDao()

    @Provides
    @Singleton
    fun provideTrackDetailDao(database: AppDatabase): TrackDetailDao = database.trackDetailDao()

    @Provides
    @Singleton
    fun provideArtistDetailDao(database: AppDatabase): ArtistDetailDao = database.artistDetailDao()

    @Provides
    @Singleton
    fun provideAlbumDetailDao(database: AppDatabase): AlbumDetailDao = database.albumDetailDao()

    @Provides
    @Singleton
    fun provideImageDao(database: AppDatabase): ImageDao = database.imageDao()
}