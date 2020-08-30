package de.schnettler.scrobbler.screens

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import androidx.compose.foundation.ScrollableColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CloudUpload
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.SettingsOverscan
import androidx.compose.material.icons.outlined.Speaker
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ContextAmbient
import androidx.core.content.ContextCompat.startActivity
import de.schnettler.composepreferences.MultiSelectListPreference
import de.schnettler.composepreferences.Preference
import de.schnettler.composepreferences.PreferenceAmbient
import de.schnettler.composepreferences.PreferenceGroup
import de.schnettler.composepreferences.SeekBarPreference
import de.schnettler.composepreferences.SwitchPreference
import de.schnettler.repo.preferences.PreferenceConstants.AUTO_SCROBBLE_DEFAULT
import de.schnettler.repo.preferences.PreferenceConstants.AUTO_SCROBBLE_KEY
import de.schnettler.repo.preferences.PreferenceConstants.SCROBBLE_CONSTRAINTS_BATTERY
import de.schnettler.repo.preferences.PreferenceConstants.SCROBBLE_CONSTRAINTS_DEFAULT
import de.schnettler.repo.preferences.PreferenceConstants.SCROBBLE_CONSTRAINTS_KEY
import de.schnettler.repo.preferences.PreferenceConstants.SCROBBLE_CONSTRAINTS_NETWORK
import de.schnettler.repo.preferences.PreferenceConstants.SCROBBLE_POINT_DEFAULT
import de.schnettler.repo.preferences.PreferenceConstants.SCROBBLE_POINT_KEY
import de.schnettler.repo.preferences.PreferenceConstants.SCROBBLE_SOURCES_KEY
import de.schnettler.repo.preferences.PreferenceConstants.SUBMIT_NOWPLAYING_DEFAULT
import de.schnettler.repo.preferences.PreferenceConstants.SUBMIT_NOWPLAYING_KEY
import de.schnettler.scrobbler.components.CustomDivider
import kotlin.math.roundToInt

@Suppress("LongMethod")
@Composable
fun SettingsScreen(modifier: Modifier = Modifier) {
    val context = ContextAmbient.current

    val mediaServices = remember {
        context.packageManager.queryIntentServices(
            Intent("android.media.browse.MediaBrowserService"),
            PackageManager.GET_RESOLVED_FILTER
        ).mapNotNull { it.serviceInfo }.associateBy({ it.packageName }, {
            it.loadLabel(context.packageManager).toString()
        })
    }

    ScrollableColumn(modifier = modifier) {
        PreferenceGroup(title = "LastFm Submission") {
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
        }

        PreferenceGroup(title = "Scrobble Settings") {
            MultiSelectListPreference(
                title = "Scrobble Sources",
                summary = "Select media apps which should be scrobbled",
                key = SCROBBLE_SOURCES_KEY,
                singleLineTitle = true,
                icon = Icons.Outlined.Speaker,
                entries = mediaServices
            )

            SeekBarPreference(
                title = "Scrobble point",
                summary = "Set the percentage of playback required for a track to scrobble",
                key = SCROBBLE_POINT_KEY,
                defaultValue = SCROBBLE_POINT_DEFAULT,
                singleLineTitle = true,
                icon = Icons.Outlined.Speed,
                steps = 4,
                valueRange = 0.5F..1F,
                valueRepresentation = { "${(it * 100).roundToInt()} %" }
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
                defaultValue = SCROBBLE_CONSTRAINTS_DEFAULT
            )
        }

        PreferenceGroup(title = "App Settings") {
            Preference(
                title = "Notifications",
                summary = "Change notification preferences",
                singleLineTitle = true,
                icon = Icons.Outlined.Notifications,
                onClick = {
                    val intent = Intent("android.settings.APP_NOTIFICATION_SETTINGS")
                        .putExtra("app_package", context.packageName) // Android 5-7
                        .putExtra("app_uid", context.applicationInfo.uid)
                        .putExtra("android.provider.extra.APP_PACKAGE", context.packageName) // Android 8+
                    startActivity(context, intent, null)
                }
            )
            CustomDivider()

            val prefs = PreferenceAmbient.current
            Preference(
                title = "Reset Preferences",
                summary = "Reset app settings to factory state",
                singleLineTitle = true,
                icon = Icons.Outlined.DeleteForever,
                onClick = {
                    prefs.sharedPreferences.edit().clear().commit()
                    if (context is Activity) context.recreate()
                }
            )
        }
    }
}