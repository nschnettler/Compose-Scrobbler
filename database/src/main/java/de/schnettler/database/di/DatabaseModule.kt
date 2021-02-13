package de.schnettler.database.di

import android.app.Application
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.schnettler.database.AppDatabase

@Suppress("TooManyFunctions")
@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {
    @Provides
    fun provideDatabase(application: Application) =
        de.schnettler.database.provideDatabase(application)

    @Provides
    fun provideUserDao(database: AppDatabase) = database.userDao()

    @Provides
    fun provideArtistDao(database: AppDatabase) = database.artistDao()

    @Provides
    fun provideAlbumDao(database: AppDatabase) = database.albumDao()

    @Provides
    fun provideTrackDao(database: AppDatabase) = database.trackDao()

    @Provides
    fun provideChartDao(database: AppDatabase) = database.chartDao()

    @Provides
    fun provideAuthDao(database: AppDatabase) = database.authDao()

    @Provides
    fun provideLocalTrackDao(database: AppDatabase) = database.localTrackDao()

    @Provides
    fun provideStatsDao(database: AppDatabase) = database.statDao()

    @Provides
    fun provideInfoDao(database: AppDatabase) = database.infoDao()

    @Provides
    fun provideRelationDao(database: AppDatabase) = database.relationDao()

    @Provides
    fun provideSessionDao(database: AppDatabase) = database.sessionDao()
}