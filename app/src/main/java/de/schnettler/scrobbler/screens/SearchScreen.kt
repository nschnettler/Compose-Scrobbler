package de.schnettler.scrobbler.screens

import androidx.compose.*
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.foundation.Box
import androidx.ui.foundation.Icon
import androidx.ui.foundation.Text
import androidx.ui.foundation.lazy.LazyColumnItems
import androidx.ui.input.ImeAction
import androidx.ui.input.TextFieldValue
import androidx.ui.layout.*
import androidx.ui.material.Divider
import androidx.ui.material.FilledTextField
import androidx.ui.material.ListItem
import androidx.ui.res.colorResource
import androidx.ui.res.vectorResource
import androidx.ui.unit.dp
import de.schnettler.database.models.Album
import de.schnettler.database.models.Artist
import de.schnettler.database.models.CommonEntity
import de.schnettler.database.models.Track
import de.schnettler.scrobbler.R
import de.schnettler.scrobbler.components.*
import de.schnettler.scrobbler.util.RefreshableUiState
import de.schnettler.scrobbler.util.formatter
import de.schnettler.scrobbler.viewmodels.SearchViewModel

@Composable
fun SearchScreen(model: SearchViewModel, onItemSelected: (CommonEntity) -> Unit) {
    val searchResult by model.state.collectAsState()
    val searchQuery by model.searchQuery.collectAsState()
    val searchInputState = state { TextFieldValue(searchQuery.query) }
    val (showSnackbarError, updateShowSnackbarError) = stateFor(searchResult) {
        searchResult is RefreshableUiState.Error
    }

    Stack(modifier = Modifier.padding(bottom = 56.dp).fillMaxSize()) {
        Column {
            Box(modifier = Modifier.padding(16.dp)) {
                FilledTextField(
                    value = searchInputState.value,
                    onValueChange = {
                        searchInputState.value = it
                        model.updateQuery(it.text)
                    },
                    label = { Text("Search") },
                    modifier = Modifier.fillMaxWidth(),
                    imeAction = ImeAction.Search,
                    onImeActionPerformed = { _, controller ->
                        controller?.hideSoftwareKeyboard()
                    }
                )
            }
            SelectableChipRow(items = listOf("Alles", "Artist", "Album", "Track"), selectedIndex = searchQuery.filter) {
                model.updateFilter(it)
            }
            searchResult.currentData?.let { searchResults ->
                LazyColumnItems(items = searchResults) {
                    when(it) {
                        is Artist -> {
                            ListItem(
                                text = { Text(it.name) },
                                secondaryText = {
                                    Text("${formatter.format(it.listeners)} Listeners")
                                },
                                icon = { PlainListIconBackground {
                                    Icon(vectorResource(id = R.drawable.ic_outline_account_circle_24))
                                } },
                                onClick = { onItemSelected(it) }
                            )
                        }
                        is Album -> {
                            ListItem(
                                text = { Text(it.name) },
                                secondaryText = {
                                    Text("${it.artist}")
                                },
                                icon = { PlainListIconBackground {
                                    Icon(vectorResource(id = R.drawable.ic_outline_album_24))
                                } },
                                onClick = { onItemSelected(it) }
                            )
                        }
                        is Track -> {
                            ListItem(
                                text = { Text(it.name) },
                                secondaryText = {
                                    Text("${it.artist}")
                                },
                                icon = { PlainListIconBackground {
                                    Icon(vectorResource(id = R.drawable.ic_round_music_note_24))
                                } },
                                onClick = { onItemSelected(it) }
                            )
                        }
                    }
                    Divider(color = colorResource(id = R.color.colorStroke), startIndent = 72.dp)
                }
            }
        }
        ErrorSnackbar(
            showError = showSnackbarError,
            onErrorAction = {  },
            text = "Can't load search results",
            onDismiss = { updateShowSnackbarError(false) },
            modifier = Modifier.gravity(Alignment.BottomCenter)
        )
    }
}