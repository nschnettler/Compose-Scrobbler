package de.schnettler.scrobbler.components

import androidx.compose.Composable
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.foundation.AdapterList
import androidx.ui.foundation.Box
import androidx.ui.foundation.ContentGravity
import androidx.ui.foundation.Text
import androidx.ui.foundation.shape.corner.CircleShape
import androidx.ui.graphics.Color
import androidx.ui.layout.fillMaxSize
import androidx.ui.layout.preferredHeight
import androidx.ui.layout.preferredWidth
import androidx.ui.layout.wrapContentWidth
import androidx.ui.material.CircularProgressIndicator
import androidx.ui.material.Divider
import androidx.ui.material.ListItem
import androidx.ui.material.Surface
import androidx.ui.res.colorResource
import androidx.ui.unit.dp
import de.schnettler.scrobbler.R
import de.schnettler.scrobbler.model.ListItem
import de.schnettler.scrobbler.util.firstLetter

@Composable
fun LiveDataLoadingComponent(modifier: Modifier = Modifier.fillMaxSize()) {
    Box(modifier = modifier, gravity = ContentGravity.Center) {
        CircularProgressIndicator(
            color = colorResource(id = R.color.colorAccent),
            modifier = Modifier.wrapContentWidth(Alignment.CenterHorizontally))
    }
}

@Composable
fun LiveDataListComponent(items: List<ListItem>) {
    AdapterList(data = items) { item ->
        ListItem(
            text = {
                Text(text = item.title)
            },
            secondaryText = {
                Text(text = item.subtitle)
            },
            icon = {
                Surface(
                    color = colorResource(id = R.color.colorBackgroundElevated),
                    shape = CircleShape,
                    modifier = Modifier.preferredHeight(40.dp) + Modifier.preferredWidth(40.dp)) {
                    Box(gravity = ContentGravity.Center) {
                        Text(text = item.title.firstLetter())
                    }
                }
            }
        )
        Divider(color = Color(0x0d000000))
    }
}