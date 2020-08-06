package de.schnettler.scrobbler.screens

import androidx.compose.Composable
import androidx.compose.collectAsState
import androidx.compose.getValue
import androidx.compose.state
import androidx.compose.stateFor
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.foundation.Box
import androidx.ui.foundation.Icon
import androidx.ui.foundation.Text
import androidx.ui.foundation.lazy.LazyColumnItems
import androidx.ui.input.ImeAction
import androidx.ui.input.TextFieldValue
import androidx.ui.layout.Column
import androidx.ui.layout.Stack
import androidx.ui.layout.fillMaxSize
import androidx.ui.layout.fillMaxWidth
import androidx.ui.layout.padding
import androidx.ui.material.FilledTextField
import androidx.ui.material.ListItem
import androidx.ui.res.vectorResource
import androidx.ui.unit.dp
import de.schnettler.database.models.BaseEntity
import de.schnettler.database.models.EntityWithStats
import de.schnettler.database.models.LastFmEntity
import de.schnettler.database.models.LastFmEntity.Album
import de.schnettler.database.models.LastFmEntity.Track
import de.schnettler.scrobbler.R
import de.schnettler.scrobbler.components.CustomDivider
import de.schnettler.scrobbler.components.ErrorSnackbar
import de.schnettler.scrobbler.components.PlainListIconBackground
import de.schnettler.scrobbler.components.SelectableChipRow
import de.schnettler.scrobbler.theme.AppColor
import de.schnettler.scrobbler.util.RefreshableUiState
import de.schnettler.scrobbler.util.formatter
import de.schnettler.scrobbler.viewmodels.SearchViewModel

@Composable
fun SearchScreen(model: SearchViewModel, onItemSelected: (LastFmEntity) -> Unit) {
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
                    },
                    backgroundColor = AppColor.BackgroundElevated
                )
            }
            SelectableChipRow(
                items = listOf("Alles", "Artist", "Album", "Track"),
                selectedIndex = searchQuery.filter
            ) {
                model.updateFilter(it)
            }

            searchResult.currentData?.let { results ->
                SearchResults(results = results, onItemSelected = onItemSelected)
            }
        }
        ErrorSnackbar(
            showError = showSnackbarError,
            onErrorAction = { },
            state = searchResult,
            fallBackMessage = "Unable to load search results",
            onDismiss = { updateShowSnackbarError(false) },
            modifier = Modifier.gravity(Alignment.BottomCenter)
        )
    }
}

@Composable
fun SearchResults(results: List<BaseEntity>, onItemSelected: (LastFmEntity) -> Unit) {
    LazyColumnItems(items = results) {
        when (it) {
            is EntityWithStats -> {
                ListItem(
                    text = { Text(it.entity.name) },
                    secondaryText = {
                        Text("${formatter.format(it.stats.listeners)} Listeners")
                    },
                    icon = {
                        PlainListIconBackground {
                            Icon(vectorResource(id = R.drawable.ic_outline_account_circle_24))
                        }
                    },
                    onClick = { onItemSelected(it.entity) }
                )
            }
            is Album -> {
                ListItem(
                    text = { Text(it.name) },
                    secondaryText = {
                        Text(it.artist)
                    },
                    icon = {
                        PlainListIconBackground {
                            Icon(vectorResource(id = R.drawable.ic_outline_album_24))
                        }
                    },
                    onClick = { onItemSelected(it) }
                )
            }
            is Track -> {
                ListItem(
                    text = { Text(it.name) },
                    secondaryText = {
                        Text(it.artist)
                    },
                    icon = {
                        PlainListIconBackground {
                            Icon(vectorResource(id = R.drawable.ic_round_music_note_24))
                        }
                    },
                    onClick = { onItemSelected(it) }
                )
            }
        }
        CustomDivider(startIndent = 72.dp)
    }
}