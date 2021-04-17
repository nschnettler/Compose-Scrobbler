package de.schnettler.scrobbler.history.ui.widget

import androidx.compose.foundation.clickable
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import de.schnettler.scrobbler.compose.widget.CustomDivider
import de.schnettler.scrobbler.history.model.ScrobbleAction
import de.schnettler.scrobbler.model.Scrobble

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SubmissionResultScrobbleItem(track: Scrobble, reason: String, onActionClicked: (ScrobbleAction) -> Unit) {
    ListItem(
        text = {
            Text(
                text = "\"${track.getNameOrPlaceholder()}\" by ${track.getArtistOrPlaceholder()}",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        secondaryText = {
            Text(
                text = reason,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        modifier = Modifier.clickable(onClick = { onActionClicked(ScrobbleAction.OPEN) })
    )
    CustomDivider()
}