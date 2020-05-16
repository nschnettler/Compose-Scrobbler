package de.schnettler.scrobbler.components

import androidx.compose.Composable
import androidx.compose.getValue
import androidx.compose.setValue
import androidx.compose.state
import androidx.ui.foundation.Icon
import androidx.ui.foundation.Text
import androidx.ui.material.BottomNavigation
import androidx.ui.material.BottomNavigationItem
import de.schnettler.scrobbler.BackStack
import de.schnettler.scrobbler.Screen
import timber.log.Timber

@Composable
fun BottomNavigationBar(items: List<Screen>) {
    val backStack = BackStack.current
    var currentScreen by state { backStack.last() }
    currentScreen = backStack.last()
    BottomNavigation() {
        items.forEachIndexed { index, screen ->
            BottomNavigationItem(
                icon = {
                    Icon(asset = screen.icon)
                },
                text = {
                    Text(text = screen.title)
                },
                selected = items.indexOf(currentScreen) == index,
                onSelected = {
                    val newScreen = items[index]
                    if (backStack.last() != newScreen) {
                        backStack.replace(items[index])
                    } else Timber.d("Already here")
                }
            )
        }
    }
}