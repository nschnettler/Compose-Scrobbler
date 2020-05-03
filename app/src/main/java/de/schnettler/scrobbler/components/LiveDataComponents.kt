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
import androidx.ui.unit.dp
import de.schnettler.scrobbler.model.ListItem

@Composable
fun LiveDataLoadingComponent() {
    Box(modifier = Modifier.fillMaxSize(), gravity = ContentGravity.Center) {
        CircularProgressIndicator(modifier = Modifier.wrapContentWidth(Alignment.CenterHorizontally))
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
                    color = Color.LightGray,
                    shape = CircleShape,
                    modifier = Modifier.preferredHeight(40.dp) + Modifier.preferredWidth(40.dp)) {
                    Box(gravity = ContentGravity.Center) {
                        Text(text = item.title[0].toString())
                    }
                }
            }
        )
        Divider(color = Color(0x0d000000))
    }
}