package de.schnettler.scrobbler.persistence.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import de.schnettler.datastore.manager.DataStoreManager
import de.schnettler.datastore.manager.dataStore
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataStoreModule {
    @Provides
    @Singleton
    fun dataStoreManager(dataStore: DataStore<Preferences>): DataStoreManager = DataStoreManager(dataStore)

    @Provides
    @Singleton
    fun dataStore(@ApplicationContext appContext: Context): DataStore<Preferences> = appContext.dataStore
}