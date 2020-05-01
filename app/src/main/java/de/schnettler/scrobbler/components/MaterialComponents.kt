package de.schnettler.scrobbler.components

import androidx.compose.Composable
import androidx.compose.getValue
import androidx.compose.setValue
import androidx.compose.state
import androidx.ui.foundation.Icon
import androidx.ui.foundation.Text
import androidx.ui.material.BottomNavigation
import androidx.ui.material.BottomNavigationItem
import de.schnettler.scrobbler.model.MenuItem

@Composable
fun BottomNavigationBar(items: List<MenuItem>) {
    var selectedIndex by state { 0 }
    BottomNavigation() {
        items.forEachIndexed { index, item ->
            BottomNavigationItem(
                icon = {
                    Icon(asset = item.icon)
                },
                text = {
                    Text(text = item.title)
                },
                // Update the selected index when the BottomNavigationItem is clicked
                selected = selectedIndex == index,
                onSelected = { selectedIndex = index }
            )
        }
    }
}