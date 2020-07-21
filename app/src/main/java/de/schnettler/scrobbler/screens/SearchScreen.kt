package de.schnettler.scrobbler.screens

import androidx.compose.Composable
import androidx.compose.collectAsState
import androidx.compose.state
import androidx.ui.core.Modifier
import androidx.ui.foundation.Box
import androidx.ui.foundation.Text
import androidx.ui.foundation.VerticalScroller
import androidx.ui.input.ImeAction
import androidx.ui.input.TextFieldValue
import androidx.ui.layout.Column
import androidx.ui.layout.fillMaxWidth
import androidx.ui.layout.padding
import androidx.ui.material.FilledTextField
import androidx.ui.unit.dp
import com.dropbox.android.external.store4.StoreResponse
import de.schnettler.database.models.Artist
import de.schnettler.database.models.CommonEntity
import de.schnettler.scrobbler.components.GenericAdapterList
import de.schnettler.scrobbler.viewmodels.SearchViewModel

@Composable
fun SearchScreen(model: SearchViewModel, onItemSelected: (CommonEntity) -> Unit) {
    val result = model.state.collectAsState()
    val query = model.query.collectAsState()
    val trackState = state { TextFieldValue(query.value) }

    Column {
        Box(modifier = Modifier.padding(16.dp)) {
            FilledTextField(
                value = trackState.value,
                onValueChange = {trackState.value = it},
                label = { Text("Search") },
                modifier = Modifier.fillMaxWidth(),
                imeAction = ImeAction.Search,
                onImeActionPerformed = { _, controller ->
                    controller?.hideSoftwareKeyboard()
                    model.updateEntry(trackState.value.text)
                }
            )
        }
        if (result.value is StoreResponse.Data) {
            GenericAdapterList(data = (result.value as StoreResponse.Data<List<Artist>>).value, onListingSelected = {
              onItemSelected(it)
            })
        }
    }
}