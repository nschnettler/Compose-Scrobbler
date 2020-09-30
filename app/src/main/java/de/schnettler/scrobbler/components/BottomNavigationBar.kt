package de.schnettler.scrobbler.components

import androidx.compose.foundation.Icon
import androidx.compose.foundation.Text
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import de.schnettler.scrobbler.AppRoute
import de.schnettler.scrobbler.util.navigationBarsPadding

@Composable
fun BottomNavigationBar(
    items: List<AppRoute>,
    currentScreen: AppRoute,
    onDestinationSelected: (AppRoute) -> Unit
) {
    CustomBottomNavigation(
        backgroundColor = MaterialTheme.colors.surface, modifier = Modifier
            .navigationBarsPadding()
    ) {
        items.forEach { screen ->
            BottomNavigationItem(
                icon = { Icon(screen.icon) },
                label = {
                    Text(
                        text = stringResource(id = screen.title),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                selected = currentScreen::class == screen::class,
                onSelect = { onDestinationSelected.invoke(screen) }
            )
        }
    }
}