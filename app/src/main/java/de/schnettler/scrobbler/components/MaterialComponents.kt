package de.schnettler.scrobbler.components

import androidx.compose.foundation.Icon
import androidx.compose.foundation.Text
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import de.schnettler.scrobbler.AppRoute

@Composable
fun BottomNavigationBar(
    items: List<AppRoute>,
    currentScreen: AppRoute,
    onDestinationSelected: (AppRoute) -> Unit
) {
    if (currentScreen !is AppRoute.DetailRoute) {
        BottomNavigation(backgroundColor = MaterialTheme.colors.surface) {
            items.forEach { screen ->
                BottomNavigationItem(
                    icon = {
                        Icon(screen.icon)
                    },
                    label = {
                        Text(text = screen.title)
                    },
                    selected = currentScreen::class == screen::class,
                    onSelect = { onDestinationSelected.invoke(screen) }
                )
            }
        }
    }
}