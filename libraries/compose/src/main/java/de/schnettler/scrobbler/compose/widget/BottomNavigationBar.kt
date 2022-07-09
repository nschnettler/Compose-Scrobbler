package de.schnettler.scrobbler.compose.widget

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import de.schnettler.scrobbler.compose.navigation.Screen

@Composable
fun BottomNavigationBar(
    screens: List<Screen>,
    currentDestination: NavDestination?,
    onClicked: (Screen) -> Unit
) {
    NavigationBar(
        contentPadding = WindowInsets.navigationBars.asPaddingValues()
    ) {
        screens.forEach { screen ->
            NavigationBarItem(
                icon = { Icon(screen.icon, null) },
                label = {
                    Text(text = stringResource(id = screen.titleId), maxLines = 1, overflow = TextOverflow.Ellipsis)
                },
                selected = currentDestination?.hierarchy?.any { it.route == screen.routeId } == true,
                onClick = {
                    if (destinationIsCurrentScreen(currentDestination, screen)) return@NavigationBarItem
                    onClicked(screen)
                }
            )
        }
    }
}

private fun destinationIsCurrentScreen(destination: NavDestination?, screen: Screen) =
    destination?.hierarchy?.first()?.route == screen.routeId