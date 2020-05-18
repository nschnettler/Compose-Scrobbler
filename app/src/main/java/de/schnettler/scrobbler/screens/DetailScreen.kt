package de.schnettler.scrobbler.screens

import androidx.compose.Composable
import androidx.compose.getValue
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.foundation.*
import androidx.ui.foundation.shape.corner.CircleShape
import androidx.ui.foundation.shape.corner.RoundedCornerShape
import androidx.ui.layout.*
import androidx.ui.livedata.observeAsState
import androidx.ui.material.Card
import androidx.ui.material.ListItem
import androidx.ui.material.MaterialTheme
import androidx.ui.material.Surface
import androidx.ui.material.ripple.ripple
import androidx.ui.res.colorResource
import androidx.ui.res.vectorResource
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
import de.schnettler.scrobbler.util.cardCornerRadius
import de.schnettler.scrobbler.util.defaultSpacerSize
import de.schnettler.scrobbler.util.firstLetter
import de.schnettler.scrobbler.viewmodels.DetailViewModel
import timber.log.Timber

@Composable
fun DetailScreen(model: DetailViewModel) {
    val info by model.details.observeAsState()

    when(val _info = info) {
        is StoreResponse.Data -> {
            VerticalScroller() {
                Card(border = Border(1.dp, colorResource(id = R.color.colorStroke)), modifier = Modifier.padding(defaultSpacerSize), shape = RoundedCornerShape(
                    cardCornerRadius
                )) {
                    ExpandingSummary(_info.value.bio, modifier = Modifier.padding(defaultSpacerSize))
                }

                Row(modifier = Modifier.fillMaxWidth() + Modifier.padding(vertical = defaultSpacerSize), horizontalArrangement = Arrangement.Center) {
                    Column(horizontalGravity = Alignment.CenterHorizontally) {
                        Icon(asset = vectorResource(id = R.drawable.ic_round_play_circle_outline_24))
                        Text(text = formatter.format(_info.value.playcount))
                    }
                    Spacer(modifier = Modifier.width(148.dp))
                    Column(horizontalGravity = Alignment.CenterHorizontally) {
                        Icon(asset = vectorResource(id = R.drawable.ic_outline_account_circle_24))
                        Text(text = formatter.format(_info.value.listeners))
                    }
                }

                TitleComponent(title = "Tags")
                ChipRow(items = _info.value.tags)

                TitleComponent(title = "Top Tracks")
                _info.value.topTracks.forEachIndexed { index, track ->
                    ListItem(
                        text = {
                            Text(track.name)
                        },
                        secondaryText = {
                            Text(formatter.format(track.listener).toString() + " Hörer")
                        },
                        icon = {
                            Surface(
                                color = colorResource(id = R.color.colorBackgroundElevated),
                                shape = CircleShape,
                                modifier = Modifier.preferredHeight(40.dp) + Modifier.preferredWidth(40.dp)) {
                                Box(gravity = ContentGravity.Center) {
                                    Text(text = "${index +1}")
                                }
                            }
                        }
                    )
                }

                TitleComponent(title = "Top Albums")
                HorizontalScrollableComponent(content = _info.value.topAlbums.sortedByDescending { it.playcount }, onEntrySelected = {
                    Timber.d("Selected")
                }, width = 136.dp, height = 136.dp, hintTextSize = 32.sp, subtitleSuffix = "Wiedergaben")

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