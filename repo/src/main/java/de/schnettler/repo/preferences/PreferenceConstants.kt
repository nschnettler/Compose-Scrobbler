package de.schnettler.repo.preferences

object PreferenceConstants  {
    const val AUTO_SCROBBLE_KEY = "scrobble_auto"
    const val AUTO_SCROBBLE_DEFAULT = true

    const val SUBMIT_NOWPLAYING_KEY = "submit_nowplaying"
    const val SUBMIT_NOWPLAYING_DEFAULT = true

    const val SCROBBLE_SOURCES_KEY = "scrobble_sources"

    const val SCROBBLE_POINT_KEY = "scrobble_point"
    const val SCROBBLE_POINT_DEFAULT = 50F

    const val SCROBBLE_CONSTRAINTS_KEY = "scrobble_constraints"
    const val SCROBBLE_CONSTRAINTS_NETWORK = "unmetered_network"
    const val SCROBBLE_CONSTRAINTS_BATTERY = "battery"
}