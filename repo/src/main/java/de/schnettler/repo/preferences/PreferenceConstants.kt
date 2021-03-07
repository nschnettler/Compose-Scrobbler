package de.schnettler.repo.preferences

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import de.schnettler.datastore.manager.PreferenceMetaData
import de.schnettler.repo.preferences.PreferenceConstants.AUTO_SCROBBLE_DEFAULT
import de.schnettler.repo.preferences.PreferenceConstants.AUTO_SCROBBLE_KEY
import de.schnettler.repo.preferences.PreferenceConstants.SCROBBLE_CONSTRAINTS_DEFAULT
import de.schnettler.repo.preferences.PreferenceConstants.SCROBBLE_CONSTRAINTS_KEY
import de.schnettler.repo.preferences.PreferenceConstants.SCROBBLE_POINT_DEFAULT
import de.schnettler.repo.preferences.PreferenceConstants.SCROBBLE_POINT_KEY
import de.schnettler.repo.preferences.PreferenceConstants.SCROBBLE_SOURCES_KEY
import de.schnettler.repo.preferences.PreferenceConstants.SUBMIT_NOWPLAYING_DEFAULT
import de.schnettler.repo.preferences.PreferenceConstants.SUBMIT_NOWPLAYING_KEY

object PreferenceConstants {
    const val AUTO_SCROBBLE_KEY = "scrobble_auto"
    const val AUTO_SCROBBLE_DEFAULT = true

    const val SUBMIT_NOWPLAYING_KEY = "submit_nowplaying"
    const val SUBMIT_NOWPLAYING_DEFAULT = true

    const val SCROBBLE_SOURCES_KEY = "scrobble_sources"

    const val SCROBBLE_POINT_KEY = "scrobble_point"
    const val SCROBBLE_POINT_DEFAULT = 0.5F

    const val SCROBBLE_CONSTRAINTS_KEY = "scrobble_constraints"
    const val SCROBBLE_CONSTRAINTS_NETWORK = "unmetered_network"
    const val SCROBBLE_CONSTRAINTS_BATTERY = "battery"
    val SCROBBLE_CONSTRAINTS_DEFAULT =
        setOf(SCROBBLE_CONSTRAINTS_BATTERY, SCROBBLE_CONSTRAINTS_NETWORK)
}

sealed class PreferenceEntry<T>(
    override val key: String,
    override val defaultValue: T,
    override val keyProvider: (String) -> Preferences.Key<T>,
) : PreferenceMetaData<T> {

    object AutoScrobble : PreferenceEntry<Boolean>(AUTO_SCROBBLE_KEY, AUTO_SCROBBLE_DEFAULT, ::booleanPreferencesKey)
    object SubmitNowPlaying :
        PreferenceEntry<Boolean>(SUBMIT_NOWPLAYING_KEY, SUBMIT_NOWPLAYING_DEFAULT, ::booleanPreferencesKey)

    object ScrobblePoint : PreferenceEntry<Float>(SCROBBLE_POINT_KEY, SCROBBLE_POINT_DEFAULT, ::floatPreferencesKey)
    object ScrobbleSources : PreferenceEntry<Set<String>>(SCROBBLE_SOURCES_KEY, emptySet(), ::stringSetPreferencesKey)
    object ScrobbleConstraints : PreferenceEntry<Set<String>>(
        SCROBBLE_CONSTRAINTS_KEY, SCROBBLE_CONSTRAINTS_DEFAULT, ::stringSetPreferencesKey
    )
}