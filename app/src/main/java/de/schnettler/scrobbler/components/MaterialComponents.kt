package de.schnettler.scrobbler.components

import androidx.compose.Composable
import androidx.compose.getValue
import androidx.compose.setValue
import androidx.compose.state
import androidx.ui.foundation.Icon
import androidx.ui.foundation.Text
import androidx.ui.material.BottomNavigation
import androidx.ui.material.BottomNavigationItem
import com.github.zsoltk.compose.router.BackStack
import de.schnettler.scrobbler.Screen
import timber.log.Timber

@Composable
fun BottomNavigationBar(backStack: BackStack<Screen>, items: List<Screen>) {
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
                        backStack.push(items[index])
                    } else Timber.d("Already here")
                }
            )
        }
    }
}