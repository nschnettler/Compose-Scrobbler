package de.schnettler.scrobbler.screens

import androidx.compose.Composable
import androidx.compose.collectAsState
import androidx.compose.state
import androidx.ui.core.Modifier
import androidx.ui.foundation.Box
import androidx.ui.foundation.Icon
import androidx.ui.foundation.Text
import androidx.ui.foundation.lazy.LazyColumnItems
import androidx.ui.input.ImeAction
import androidx.ui.input.TextFieldValue
import androidx.ui.layout.Column
import androidx.ui.layout.fillMaxWidth
import androidx.ui.layout.padding
import androidx.ui.material.Divider
import androidx.ui.material.FilledTextField
import androidx.ui.material.ListItem
import androidx.ui.res.colorResource
import androidx.ui.res.vectorResource
import androidx.ui.unit.dp
import com.dropbox.android.external.store4.StoreResponse
import de.schnettler.database.models.Artist
import de.schnettler.database.models.CommonEntity
import de.schnettler.scrobbler.R
import de.schnettler.scrobbler.components.PlainListIconBackground
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
            LazyColumnItems(items = (result.value as StoreResponse.Data<List<Artist>>).value) {
                ListItem(
                    text = { Text(it.name) },
                    secondaryText = { Text("${formatter.format(it.listeners)} Listeners") },
                    icon = { PlainListIconBackground {
                        Icon(vectorResource(id = R.drawable.ic_outline_account_circle_24))
                    } },
                    onClick = { onItemSelected(it) }
                )
                Divider(color = colorResource(id = R.color.colorStroke), startIndent = 72.dp)
            }
        }
    }
}