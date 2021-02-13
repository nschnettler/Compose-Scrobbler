package de.schnettler.scrobbler.ui.settings

import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.ScrollableColumn
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CloudUpload
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.SettingsOverscan
import androidx.compose.material.icons.outlined.Speaker
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat.startActivity
import de.schnettler.composepreferences.LocalPreferences
import de.schnettler.composepreferences.MultiSelectListPreference
import de.schnettler.composepreferences.Preference
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
import de.schnettler.scrobbler.ui.common.compose.widget.CustomDivider
import dev.chrisbanes.accompanist.insets.statusBarsHeight
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterialApi::class)
@Suppress("LongMethod")
@Composable
fun SettingsScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    val mediaServices = mutableStateMapOf<String, String>()
    val constraints: Map<String, Int> = remember {
        mapOf(
            SCROBBLE_CONSTRAINTS_NETWORK to R.string.setting_list_scrobbleconstraints_network,
            SCROBBLE_CONSTRAINTS_BATTERY to R.string.setting_list_scrobbleconstraints_battery,
        )
    }
    val scope = rememberCoroutineScope()

    scope.launch { mediaServices.putAll(context.getMediaBrowserServices()) }

    ScrollableColumn(modifier = modifier) {
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.statusBarsHeight())
        PreferenceGroup(title = stringResource(id = R.string.setting_group_submission)) {
            SwitchPreference(
                title = stringResource(id = R.string.setting_switch_autoscrobble_title),
                summary = stringResource(id = R.string.setting_switch_autoscrobble_description),
                key = AUTO_SCROBBLE_KEY,
                singleLineTitle = true,
                icon = Icons.Outlined.CloudUpload,
                defaultValue = AUTO_SCROBBLE_DEFAULT
            )

            SwitchPreference(
                title = stringResource(id = R.string.setting_switch_submitnp_title),
                summary = stringResource(id = R.string.setting_switch_submitnp_description),
                key = SUBMIT_NOWPLAYING_KEY,
                singleLineTitle = true,
                icon = Icons.Outlined.MusicNote,
                defaultValue = SUBMIT_NOWPLAYING_DEFAULT
            )
        }

        PreferenceGroup(title = stringResource(id = R.string.settings_group_scrobble)) {
            MultiSelectListPreference(
                title = stringResource(id = R.string.setting_list_scrobblesource_title),
                summary = stringResource(id = R.string.setting_list_scrobblesource_description),
                key = SCROBBLE_SOURCES_KEY,
                singleLineTitle = true,
                icon = Icons.Outlined.Speaker,
                entries = mediaServices
            )

            SeekBarPreference(
                title = stringResource(id = R.string.setting_seek_scrobblepoint_title),
                summary = stringResource(id = R.string.setting_seek_scrobblepoint_description),
                key = SCROBBLE_POINT_KEY,
                defaultValue = SCROBBLE_POINT_DEFAULT,
                singleLineTitle = true,
                icon = Icons.Outlined.Speed,
                steps = 4,
                valueRange = 0.5F..1F,
                valueRepresentation = { "${(it * 100).roundToInt()} %" }
            )

            MultiSelectListPreference(
                title = stringResource(id = R.string.setting_list_scrobbleconstraints_title),
                summary = stringResource(id = R.string.setting_list_scrobbleconstraints_description),
                key = SCROBBLE_CONSTRAINTS_KEY,
                singleLineTitle = true,
                icon = Icons.Outlined.SettingsOverscan,
                entries = constraints.mapValues { stringResource(id = it.value) },
                defaultValue = SCROBBLE_CONSTRAINTS_DEFAULT
            )
        }

        PreferenceGroup(title = stringResource(id = R.string.setting_group_misc)) {
            Preference(
                title = stringResource(id = R.string.setting_notifications_title),
                summary = stringResource(id = R.string.setting_notifications_description),
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

            val prefs = LocalPreferences.current
            Preference(
                title = stringResource(id = R.string.setting_reset_title),
                summary = stringResource(id = R.string.setting_reset_description),
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