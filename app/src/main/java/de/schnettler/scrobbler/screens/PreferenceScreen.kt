package de.schnettler.scrobbler.screens

import android.app.Activity
import androidx.compose.Composable
import androidx.ui.core.ContextAmbient
import androidx.ui.foundation.ScrollableColumn
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.outlined.CloudUpload
import androidx.ui.material.icons.outlined.DeleteForever
import androidx.ui.material.icons.outlined.MusicNote
import androidx.ui.material.icons.outlined.SettingsOverscan
import androidx.ui.material.icons.outlined.Speaker
import androidx.ui.material.icons.outlined.Speed
import de.schnettler.composepreferences.AmbientPreferences
import de.schnettler.composepreferences.MultiSelectListPreference
import de.schnettler.composepreferences.Preference
import de.schnettler.composepreferences.SeekBarPreference
import de.schnettler.composepreferences.SwitchPreference
import de.schnettler.repo.preferences.PreferenceConstants.AUTO_SCROBBLE_DEFAULT
import de.schnettler.repo.preferences.PreferenceConstants.AUTO_SCROBBLE_KEY
import de.schnettler.repo.preferences.PreferenceConstants.SCROBBLE_CONSTRAINTS_BATTERY
import de.schnettler.repo.preferences.PreferenceConstants.SCROBBLE_CONSTRAINTS_KEY
import de.schnettler.repo.preferences.PreferenceConstants.SCROBBLE_CONSTRAINTS_NETWORK
import de.schnettler.repo.preferences.PreferenceConstants.SCROBBLE_POINT_DEFAULT
import de.schnettler.repo.preferences.PreferenceConstants.SCROBBLE_POINT_KEY
import de.schnettler.repo.preferences.PreferenceConstants.SCROBBLE_SOURCES_KEY
import de.schnettler.repo.preferences.PreferenceConstants.SUBMIT_NOWPLAYING_DEFAULT
import de.schnettler.repo.preferences.PreferenceConstants.SUBMIT_NOWPLAYING_KEY
import de.schnettler.scrobbler.components.CustomDivider

@Composable
fun PreferenceScreen() {
    ScrollableColumn {
        SwitchPreference(
            title = "Auto Scrobble",
            summary = "Automatically submit scrobbles",
            key = AUTO_SCROBBLE_KEY,
            singleLineTitle = true,
            icon = Icons.Outlined.CloudUpload,
            defaultValue = AUTO_SCROBBLE_DEFAULT
        )

        SwitchPreference(
            title = "Submit NowPlaying",
            summary = "Submit nowplaying track to last.fm",
            key = SUBMIT_NOWPLAYING_KEY,
            singleLineTitle = true,
            icon = Icons.Outlined.MusicNote,
            defaultValue = SUBMIT_NOWPLAYING_DEFAULT
        )

        MultiSelectListPreference(
            title = "Scrobble Sources",
            summary = "Select media apps which should be scrobbled",
            key = SCROBBLE_SOURCES_KEY,
            singleLineTitle = true,
            icon = Icons.Outlined.Speaker,
            entries = mapOf(
                "com.youtube.music" to "YoutubeMusic",
                "com.google.music" to "PlayMusic",
                "com.spotify" to "Spotify",
                "com.deezer" to "Deezer"
            )
        )

        SeekBarPreference(
            title = "Scrobble point",
            summary = "Set the percentage of playback required for a track to scrobble",
            key = SCROBBLE_POINT_KEY,
            defaultValue = SCROBBLE_POINT_DEFAULT,
            singleLineTitle = true,
            icon = Icons.Outlined.Speed,
            steps = 4,
            valueRange = 50F..100F
        )

        val constraints = mapOf(
            SCROBBLE_CONSTRAINTS_NETWORK to "Unmetered Network",
            SCROBBLE_CONSTRAINTS_BATTERY to "Battery not low",
        )
        MultiSelectListPreference(
            title = "Scrobble Constraints",
            summary = "Set constraints which need to be met before submitting scrobbles to last.fm",
            key = SCROBBLE_CONSTRAINTS_KEY,
            singleLineTitle = true,
            icon = Icons.Outlined.SettingsOverscan,
            entries = constraints,
            defaultValue = setOf(SCROBBLE_CONSTRAINTS_BATTERY, SCROBBLE_CONSTRAINTS_NETWORK)
        )

        CustomDivider()

        val prefs = AmbientPreferences.current
        val context = ContextAmbient.current
        Preference(
            title = "Reset Preferences",
            summary = "Reset app settings to factory state",
            key = "setttings_reset",
            singleLineTitle = true,
            icon = Icons.Outlined.DeleteForever,
            onClick = {
                prefs.sharedPreferences.edit().clear().commit()
                if (context is Activity) context.recreate()
            }
        )
    }
}