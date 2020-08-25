package de.schnettler.scrobbler.screens

import androidx.compose.foundation.Box
import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.preferredSize
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Card
import androidx.compose.material.ListItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.Surface
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.PlayCircleOutline
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.schnettler.common.TimePeriod
import de.schnettler.database.models.User
import de.schnettler.scrobbler.UIAction
import de.schnettler.scrobbler.UIError
import de.schnettler.scrobbler.components.StatsRow
import de.schnettler.scrobbler.components.SwipeRefreshProgressIndicator
import de.schnettler.scrobbler.components.SwipeToRefreshLayout
import de.schnettler.scrobbler.components.TopListScroller
import de.schnettler.scrobbler.theme.AppColor
import de.schnettler.scrobbler.util.defaultSpacerSize
import de.schnettler.scrobbler.util.toFlagEmoji
import de.schnettler.scrobbler.viewmodels.UserViewModel
import dev.chrisbanes.accompanist.coil.CoilImage
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun ProfileScreen(
    model: UserViewModel,
    actionHandler: (UIAction) -> Unit,
    errorHandler: @Composable (UIError) -> Unit,
    modifier: Modifier = Modifier
) {

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

    if (states.any { it.isError }) {
        errorHandler(UIError.ShowErrorSnackbar(
            state = states.firstOrNull { it.isError },
            fallbackMessage = "Unable to refresh history",
            onAction = model::refresh
        ))
    }

    SwipeToRefreshLayout(
        refreshingState = states.any { it.isRefreshing },
        onRefresh = model::refresh,
        refreshIndicator = { SwipeRefreshProgressIndicator() }
    ) {
        ScrollableColumn(modifier = modifier.fillMaxSize(), children = {
            userState.currentData?.let {
                UserInfoComponent(it)
            }
            TopListScroller(
                title = "Top-Künstler (${timePeriod.niceName})",
                state = artistState,
                actionHandler = actionHandler
            )
            TopListScroller(
                title = "Top-Alben",
                state = albumState,
                actionHandler = actionHandler
            )
            TopListScroller(
                title = "Top-Titel",
                state = trackState,
                actionHandler = actionHandler
            )
        })
    }
}

@Composable
fun UserInfoComponent(user: User) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(
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
    var selected by mutableStateOf(model.timePeriod.value)
    val radioGroupOptions = TimePeriod.values().asList()
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(text = "Zeitrahmen") },
        text = {
            Column {
                radioGroupOptions.forEach { current ->
                    Row(Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = (current == selected),
                            onClick = { selected = current }
                        )
                        .padding(16.dp)
                    ) {
                        RadioButton(
                            selected = (current == selected),
                            onClick = { selected = current }
                        )
                        Text(
                            text = current.niceName,
                            style = MaterialTheme.typography.body1.merge(),
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
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