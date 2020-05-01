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
    var currentScreen by state { Screen.Charts as Screen }
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
                    //Navigate
                    currentScreen = items[index]
                    Timber.d("Screen: ${currentScreen.title}")
                    backStack.push(currentScreen)
                }
            )
        }
    }
}