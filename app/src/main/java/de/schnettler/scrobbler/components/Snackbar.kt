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
import de.schnettler.scrobbler.theme.PADDING_16
import de.schnettler.scrobbler.util.RefreshableUiState
import kotlinx.coroutines.delay

@Composable
fun ErrorSnackbar(
    showError: Boolean,
    modifier: Modifier = Modifier,
    state: RefreshableUiState<Any>?,
    fallBackMessage: String = "An unknown error has occurred",
    onErrorAction: () -> Unit = { },
    onDismiss: () -> Unit = { }
) {
    if (showError && state is RefreshableUiState.Error) {
        // Make Snackbar disappear after 5 seconds if the user hasn't interacted with it
        launchInComposition {
            delay(timeMillis = 5000L)
            onDismiss()
        }

        Snackbar(
            modifier = modifier.padding(PADDING_16),
            text = { Text(text = state.errorMessage ?: state.exception?.message ?: fallBackMessage) },
            action = {
                TextButton(
                    onClick = {
                        onErrorAction()
                        onDismiss()
                    },
                    contentColor = contentColor()
                ) {
                    Text(
                        text = "Retry",
                        color = MaterialTheme.colors.secondary
                    )
                }
            }
        )
    }
}