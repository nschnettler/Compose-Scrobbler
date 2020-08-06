package de.schnettler.scrobbler.screens

import androidx.compose.Composable
import androidx.compose.collectAsState
import androidx.compose.getValue
import androidx.compose.setValue
import androidx.compose.state
import androidx.compose.stateFor
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.foundation.Box
import androidx.ui.foundation.ScrollableColumn
import androidx.ui.foundation.Text
import androidx.ui.foundation.selection.selectable
import androidx.ui.foundation.shape.corner.CircleShape
import androidx.ui.layout.Column
import androidx.ui.layout.Row
import androidx.ui.layout.Stack
import androidx.ui.layout.fillMaxSize
import androidx.ui.layout.fillMaxWidth
import androidx.ui.layout.padding
import androidx.ui.layout.preferredSize
import androidx.ui.material.AlertDialog
import androidx.ui.material.Card
import androidx.ui.material.ListItem
import androidx.ui.material.MaterialTheme
import androidx.ui.material.RadioButton
import androidx.ui.material.RadioGroup
import androidx.ui.material.Surface
import androidx.ui.material.TextButton
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.outlined.Face
import androidx.ui.material.icons.rounded.FavoriteBorder
import androidx.ui.material.icons.rounded.PlayCircleOutline
import androidx.ui.unit.dp
import de.schnettler.common.TimePeriod
import de.schnettler.database.models.LastFmEntity
import de.schnettler.database.models.User
import de.schnettler.scrobbler.components.ErrorSnackbar
import de.schnettler.scrobbler.components.StatsRow
import de.schnettler.scrobbler.components.SwipeRefreshPrograssIndicator
import de.schnettler.scrobbler.components.SwipeToRefreshLayout
import de.schnettler.scrobbler.components.TopListScroller
import de.schnettler.scrobbler.theme.AppColor
import de.schnettler.scrobbler.util.defaultSpacerSize
import de.schnettler.scrobbler.util.toFlagEmoji
import de.schnettler.scrobbler.viewmodels.UserViewModel
import dev.chrisbanes.accompanist.coil.CoilImage
import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle

@Composable
fun ProfileScreen(model: UserViewModel, onListingSelected: (LastFmEntity) -> Unit) {

    val userState by model.userState.collectAsState()
    val artistState by model.artistState.collectAsState()
    val albumState by model.albumState.collectAsState()
    val trackState by model.trackState.collectAsState()
    val states = listOf(userState, artistState, albumState, trackState)

    val timePeriod by model.timePeriod.collectAsState()
    val showDialog by model.showFilterDialog.collectAsState()

    if (showDialog) {
        PeriodSelectDialog(onSelect = { newTimePeriod ->
            model.updatePeriod(newTimePeriod)
            model.showDialog(false)
        }, onDismiss = {
            model.showDialog(false)
        }, model = model)
    }

    Stack(modifier = Modifier.padding(bottom = 56.dp).fillMaxSize()) {
        val (showSnackbarError, updateShowSnackbarError) = stateFor(states) {
            states.any { it.isError }
        }
        SwipeToRefreshLayout(
            refreshingState = states.any { it.isRefreshing },
            onRefresh = { model.refresh() },
            refreshIndicator = { SwipeRefreshPrograssIndicator() }
        ) {
                ScrollableColumn(modifier = Modifier.fillMaxSize(), children = {
                    userState.currentData?.let {
                        UserInfoComponent(it)
                    }
                    TopListScroller(
                        title = "Top-Künstler (${timePeriod.niceName})",
                        state = artistState,
                        onEntrySelected = onListingSelected
                    )
                    TopListScroller(
                        title = "Top-Alben",
                        state = albumState,
                        onEntrySelected = onListingSelected
                    )
                    TopListScroller(
                        title = "Top-Titel",
                        state = trackState,
                        onEntrySelected = onListingSelected
                    )
                })
        }
        ErrorSnackbar(
            showError = showSnackbarError,
            onErrorAction = { model.refresh() },
            onDismiss = { updateShowSnackbarError(false) },
            state = states.firstOrNull { it.isError },
            fallBackMessage = "Unable to refresh history",
            modifier = Modifier.gravity(Alignment.BottomCenter)
        )
    }
}

@Composable
fun UserInfoComponent(user: User) {
    Card(
        modifier = Modifier.fillMaxWidth() + Modifier.padding(
            start = defaultSpacerSize,
            end = defaultSpacerSize,
            top = defaultSpacerSize
        )
    ) {
        val date: LocalDateTime = Instant.ofEpochSecond(user.registerDate)
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime()
        Column {
            ListItem(
                text = {
                    Text(text = "${user.name} ${user.countryCode.toFlagEmoji()}")
                },
                secondaryText = {
                    Text(
                        text = "${user.realname}\nscrobbelt seit ${
                            DateTimeFormatter.ofLocalizedDate(
                                FormatStyle.LONG
                            ).format(date)
                        }"
                    )
                },
                icon = {
                    Surface(color = AppColor.BackgroundElevated, shape = CircleShape) {
                        Box(Modifier.preferredSize(56.dp)) {
                            if (user.imageUrl.isNotEmpty()) {
                                CoilImage(data = user.imageUrl, modifier = Modifier.fillMaxSize())
                            }
                        }
                    }
                }
            )

            StatsRow(
                items = listOf(
                    Icons.Rounded.PlayCircleOutline to user.playcount,
                    Icons.Outlined.Face to user.artistCount,
                    Icons.Rounded.FavoriteBorder to user.lovedTracksCount
                )
            )
        }
    }
}

@Composable
private fun PeriodSelectDialog(
    onSelect: (selected: TimePeriod) -> Unit,
    onDismiss: () -> Unit,
    model: UserViewModel
) {
    var selected by state { model.timePeriod.value }
    val radioGroupOptions = TimePeriod.values().asList()
    AlertDialog(
        onCloseRequest = { onDismiss() },
        title = { Text(text = "Zeitrahmen") },
        text = {
            RadioGroup {
                radioGroupOptions.forEach {
                    val isSelected = selected == it
                    val onSelected = {
                        selected = it
                    }
                    Box(
                        modifier = Modifier.selectable(
                            selected = isSelected,
                            onClick = { if (!isSelected) onSelected() }
                        ),
                        children = {
                            Box {
                                Row(Modifier.fillMaxWidth().padding(16.dp)) {
                                    RadioButton(selected = isSelected, onClick = onSelected)
                                    Text(
                                        text = it.niceName,
                                        style = MaterialTheme.typography.body1.merge(),
                                        modifier = Modifier.padding(start = 16.dp)
                                    )
                                }
                            }
                        }
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onSelect(selected) },
                contentColor = MaterialTheme.colors.secondary
            ) {
                Text(text = "Select")
            }
        }
    )
}