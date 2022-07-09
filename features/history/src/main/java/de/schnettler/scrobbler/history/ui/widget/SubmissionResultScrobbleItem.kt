package de.schnettler.scrobbler.history.ui.widget

import androidx.compose.foundation.clickable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import de.schnettler.scrobbler.compose.widget.CustomDivider
import de.schnettler.scrobbler.compose.widget.MaterialListItem
import de.schnettler.scrobbler.history.model.ScrobbleAction
import de.schnettler.scrobbler.model.Scrobble

@Composable
fun SubmissionResultScrobbleItem(track: Scrobble, reason: String, onActionClicked: (ScrobbleAction) -> Unit) {
    MaterialListItem(
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