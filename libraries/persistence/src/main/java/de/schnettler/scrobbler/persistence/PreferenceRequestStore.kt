package de.schnettler.scrobbler.persistence

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import de.schnettler.datastore.manager.PreferenceRequest

object PreferenceRequestStore {
    const val SCROBBLE_CONSTRAINTS_NETWORK = "unmetered_network"
    const val SCROBBLE_CONSTRAINTS_BATTERY = "battery"

    val autoScrobble = PreferenceRequest(
        key = booleanPreferencesKey("scrobble_auto"),
        defaultValue = true,
    )
    val submitNowPlaying = PreferenceRequest(
        key = booleanPreferencesKey("submit_nowplaying"),
        defaultValue = true,
    )
    val scrobblePoint = PreferenceRequest(
        key = floatPreferencesKey("scrobble_point"),
        defaultValue = 0.5F,
    )
    val scrobbleSources = PreferenceRequest(
        key = stringSetPreferencesKey("scrobble_sources"),
        defaultValue = emptySet(),
    )
    val scrobbleConstraints = PreferenceRequest(
        key = stringSetPreferencesKey("scrobble_constraints"),
        defaultValue = setOf(SCROBBLE_CONSTRAINTS_BATTERY, SCROBBLE_CONSTRAINTS_NETWORK),
    )
    val mediaCardSize = PreferenceRequest(
        stringPreferencesKey("media_card_size"),
        "MEDIUM",
    )
}