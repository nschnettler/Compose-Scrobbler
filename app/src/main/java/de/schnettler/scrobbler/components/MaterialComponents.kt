package de.schnettler.scrobbler.components

import androidx.compose.Composable
import androidx.ui.foundation.Icon
import androidx.ui.foundation.Text
import androidx.ui.material.BottomNavigation
import androidx.ui.material.BottomNavigationItem
import androidx.ui.res.vectorResource
import com.koduok.compose.navigation.core.BackStack
import de.schnettler.scrobbler.AppRoute
import timber.log.Timber

@Composable
fun BottomNavigationBar(items: List<AppRoute>, backStack: BackStack<AppRoute>) {
    val currentScreen = backStack.current.data

    if (currentScreen !is AppRoute.DetailRoute) {
        BottomNavigation() {
            items.forEach {screen ->
                Timber.d("ScreenInfo: ${screen.title}, selected: ${screen === currentScreen }")
                BottomNavigationItem(
                    icon = {
                        Icon(asset = vectorResource(id = screen.icon))
                    },
                    text = {
                        Text(text = screen.title)
                    },
                    selected = currentScreen::class == screen::class,
                    onSelected = {
                        backStack.replace(screen)
                    }
                )
            }
        }
    }
}