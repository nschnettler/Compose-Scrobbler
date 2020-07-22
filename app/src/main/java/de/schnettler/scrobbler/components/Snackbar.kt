package de.schnettler.scrobbler.components

import androidx.compose.Composable
import androidx.compose.launchInComposition
import androidx.ui.core.Modifier
import androidx.ui.foundation.Text
import androidx.ui.foundation.contentColor
import androidx.ui.layout.padding
import androidx.ui.material.MaterialTheme
import androidx.ui.material.Snackbar
import androidx.ui.material.TextButton
import androidx.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun ErrorSnackbar(
        showError: Boolean,
        modifier: Modifier = Modifier,
        text: String = "Can't update history",
        onErrorAction: () -> Unit = { },
        onDismiss: () -> Unit = { }
) {
    if (showError) {
        // Make Snackbar disappear after 5 seconds if the user hasn't interacted with it
        launchInComposition {
            delay(timeMillis = 5000L)
            onDismiss()
        }

        Snackbar(
                modifier = modifier.padding(16.dp),
                text = { Text(text) },
                action = {
                    TextButton(
                            onClick = {
                                onErrorAction()
                                onDismiss()
                            },
                            contentColor = contentColor()
                    ) {
                        Text(
                                text = "RETRY",
                                color = MaterialTheme.colors.secondary
                        )
                    }
                }
        )
    }
}