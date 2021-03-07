package de.schnettler.scrobbler.ui.settings

import android.content.Context
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CloudUpload
import androidx.compose.material.icons.outlined.MusicNote
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
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import de.schnettler.datastore.compose.PreferenceScreen
import de.schnettler.datastore.compose.model.MultiListPreferenceItem
import de.schnettler.datastore.compose.model.SeekbarPreferenceItem
import de.schnettler.datastore.compose.model.SwitchPreferenceItem
import de.schnettler.repo.preferences.PreferenceConstants.SCROBBLE_CONSTRAINTS_BATTERY
import de.schnettler.repo.preferences.PreferenceConstants.SCROBBLE_CONSTRAINTS_DEFAULT
import de.schnettler.repo.preferences.PreferenceConstants.SCROBBLE_CONSTRAINTS_NETWORK
import de.schnettler.repo.preferences.PreferenceConstants.SCROBBLE_POINT_DEFAULT
import de.schnettler.repo.preferences.PreferenceEntry
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

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

    PreferenceScreen(
        modifier = modifier,
        statusBarPadding = true,
        items = listOf(
            SwitchPreferenceItem(
                PreferenceEntry.AutoScrobble,
                title = stringResource(id = R.string.setting_switch_autoscrobble_title),
                summary = stringResource(id = R.string.setting_switch_autoscrobble_description),
                singleLineTitle = true,
                icon = Icons.Outlined.CloudUpload,
            ),
            SwitchPreferenceItem(
                PreferenceEntry.SubmitNowPlaying,
                title = stringResource(id = R.string.setting_switch_submitnp_title),
                summary = stringResource(id = R.string.setting_switch_submitnp_description),
                singleLineTitle = true,
                icon = Icons.Outlined.MusicNote,
            ),
            // Group 2
            MultiListPreferenceItem(
                PreferenceEntry.ScrobbleSources,
                title = stringResource(id = R.string.setting_list_scrobblesource_title),
                summary = stringResource(id = R.string.setting_list_scrobblesource_description),
                singleLineTitle = true,
                icon = Icons.Outlined.Speaker,
                entries = mediaServices
            ),
            SeekbarPreferenceItem(
                PreferenceEntry.ScrobblePoint,
                title = stringResource(id = R.string.setting_seek_scrobblepoint_title),
                summary = stringResource(id = R.string.setting_seek_scrobblepoint_description),
                defaultValue = SCROBBLE_POINT_DEFAULT,
                singleLineTitle = true,
                icon = Icons.Outlined.Speed,
                steps = 4,
                valueRange = 0.5F..1F,
                valueRepresentation = { "${(it * 100).roundToInt()} %" }
            ),
            MultiListPreferenceItem(
                PreferenceEntry.ScrobbleConstraints,
                title = stringResource(id = R.string.setting_list_scrobbleconstraints_title),
                summary = stringResource(id = R.string.setting_list_scrobbleconstraints_description),
                singleLineTitle = true,
                icon = Icons.Outlined.SettingsOverscan,
                entries = constraints.mapValues { stringResource(id = it.value) },
                defaultValue = SCROBBLE_CONSTRAINTS_DEFAULT
            ),

            // GROUP 3
        )
    )
}