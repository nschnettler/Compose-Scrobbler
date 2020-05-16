package de.schnettler.scrobbler.screens

import androidx.compose.Composable
import androidx.compose.getValue
import androidx.ui.core.Modifier
import androidx.ui.core.clipToBounds
import androidx.ui.foundation.*
import androidx.ui.foundation.shape.corner.RoundedCornerShape
import androidx.ui.layout.*
import androidx.ui.livedata.observeAsState
import androidx.ui.material.MaterialTheme
import androidx.ui.material.ripple.ripple
import androidx.ui.res.colorResource
import androidx.ui.text.style.TextOverflow
import androidx.ui.unit.dp
import androidx.ui.unit.sp
import com.dropbox.android.external.store4.StoreResponse
import de.schnettler.scrobbler.BackStack
import de.schnettler.scrobbler.R
import de.schnettler.scrobbler.Screen
import de.schnettler.scrobbler.components.ExpandingSummary
import de.schnettler.scrobbler.components.LiveDataLoadingComponent
import de.schnettler.scrobbler.components.TitleComponent
import de.schnettler.scrobbler.util.defaultSpacerSize
import de.schnettler.scrobbler.viewmodels.DetailViewModel
import timber.log.Timber

@Composable
fun DetailScreen(model: DetailViewModel) {
    val info by model.details.observeAsState()

    when(val _info = info) {
        is StoreResponse.Data -> {
            VerticalScroller() {
                ExpandingSummary(_info.value.bio, modifier = Modifier.padding(defaultSpacerSize))

                TitleComponent(title = "Tags")
                ChipRow(items = _info.value.tags)

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

@Composable
fun ChipRow(items: List<String>) {
    Box(modifier = Modifier.padding(horizontal = 16.dp)) {
        FlowRow(mainAxisSpacing = 8.dp, crossAxisSpacing = 16.dp) {
            items.forEach {
                Clickable(onClick = {}, modifier = Modifier.ripple()) {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.body2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.drawBackground(
                            color = colorResource(id = R.color.colorBackgroundElevated),
                            shape = RoundedCornerShape(25.dp)
                        ) + Modifier.padding(horizontal = 12.dp, vertical = 8.dp) + Modifier.fillMaxHeight()
                    )
                }
            }
        }
    }
}