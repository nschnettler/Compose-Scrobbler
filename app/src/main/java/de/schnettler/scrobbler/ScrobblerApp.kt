package de.schnettler.scrobbler

import android.app.Application
import timber.log.Timber

class ScrobblerApp: Application() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }
}