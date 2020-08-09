package de.schnettler.scrobbler.components

import androidx.compose.foundation.Text
import androidx.compose.foundation.contentColor
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Snackbar
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.launchInComposition
import androidx.compose.ui.Modifier
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