package de.schnettler.repo.di

import android.app.Application
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import de.schnettler.database.AppDatabase
import javax.inject.Singleton

@Suppress("TooManyFunctions")
@Module
@InstallIn(ApplicationComponent::class)
class DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(application: Application) =
        de.schnettler.database.provideDatabase(application)

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
    fun provideLocalTrackDao(database: AppDatabase) = database.localTrackDao()

    @Provides
    @Singleton
    fun provideStatsDao(database: AppDatabase) = database.statDao()

    @Provides
    @Singleton
    fun provideInfoDao(database: AppDatabase) = database.infoDao()

    @Provides
    @Singleton
    fun provideRelationDao(database: AppDatabase) = database.relationDao()
}