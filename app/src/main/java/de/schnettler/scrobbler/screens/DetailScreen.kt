package de.schnettler.scrobbler.screens

import androidx.compose.Composable
import androidx.compose.getValue
import androidx.ui.core.Modifier
import androidx.ui.foundation.Text
import androidx.ui.foundation.VerticalScroller
import androidx.ui.layout.padding
import androidx.ui.livedata.observeAsState
import androidx.ui.unit.dp
import androidx.ui.unit.sp
import com.dropbox.android.external.store4.StoreResponse
import de.schnettler.scrobbler.BackStack
import de.schnettler.scrobbler.Screen
import de.schnettler.scrobbler.components.ExpandingSummary
import de.schnettler.scrobbler.components.LiveDataLoadingComponent
import de.schnettler.scrobbler.components.TitleComponent
import de.schnettler.scrobbler.viewmodels.DetailViewModel
import timber.log.Timber

@Composable
fun DetailScreen(model: DetailViewModel) {
    val info by model.details.observeAsState()

    when(val _info = info) {
        is StoreResponse.Data -> {
            VerticalScroller() {
                ExpandingSummary(_info.value.bio, modifier = Modifier.padding(16.dp))

                TitleComponent(title = "Ähnliche Künstler")

                val backstack = BackStack.current
                HorizontalScrollableComponent(content = _info.value.similar, onEntrySelected = {
                    backstack.push(Screen.Detail(it))
                }, width = 104.dp, height = 104.dp, hintTextSize = 32.sp)
            }
        }
        is StoreResponse.Loading -> {
            LiveDataLoadingComponent()
        }
        is StoreResponse.Error -> {
            Text(text = _info.errorMessageOrNull() ?: "")
        }
    }
}