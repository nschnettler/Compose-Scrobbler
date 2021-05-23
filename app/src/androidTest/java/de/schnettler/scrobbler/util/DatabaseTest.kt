package de.schnettler.scrobbler.util

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import de.schnettler.scrobbler.db.AppDatabase
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith

@Suppress("UnnecessaryAbstractClass")
@RunWith(AndroidJUnit4::class)
abstract class DatabaseTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    lateinit var db: AppDatabase

    @Before
    fun initDatabase() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).build()
    }

    @After
    fun closeDatabase() = db.close()
}