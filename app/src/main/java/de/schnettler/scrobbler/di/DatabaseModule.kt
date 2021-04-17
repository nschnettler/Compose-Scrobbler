package de.schnettler.scrobbler.di

import android.app.Application
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.schnettler.scrobbler.AppDatabase
import de.schnettler.scrobbler.getDatabase
import javax.inject.Singleton

@Suppress("TooManyFunctions")
@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(application: Application) =
        getDatabase(application)

    @Provides
    @Singleton
    fun provideUserDao(database: AppDatabase) = database.userDao()

    @Provides
    @Singleton
    fun provideArtistDao(database: AppDatabase) = database.artistDao()

    @Provides
    @Singleton
    fun provideAlbumDao(database: AppDatabase) = database.albumDao()

    @Provides
    @Singleton
    fun provideTrackDao(database: AppDatabase) = database.trackDao()

    @Provides
    @Singleton
    fun provideChartDao(database: AppDatabase) = database.chartDao()

    @Provides
    @Singleton
    fun provideAuthDao(database: AppDatabase) = database.authDao()

    @Provides
    @Singleton
    fun provideSubmissionDao(database: AppDatabase) = database.submissionDao()

    @Provides
    @Singleton
    fun provideHistoryDao(database: AppDatabase) = database.historyDao()

    @Provides
    @Singleton
    fun provideStatsDao(database: AppDatabase) = database.statDao()

    @Provides
    @Singleton
    fun provideInfoDao(database: AppDatabase) = database.infoDao()

    @Provides
    @Singleton
    fun provideRelationDao(database: AppDatabase) = database.relationDao()

    @Provides
    @Singleton
    fun provideSessionDao(database: AppDatabase) = database.sessionDao()
}