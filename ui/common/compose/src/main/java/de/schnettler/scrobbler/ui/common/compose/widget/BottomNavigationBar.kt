package de.schnettler.scrobbler.ui.common.compose.widget

import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.google.accompanist.insets.navigationBarsPadding
import de.schnettler.scrobbler.ui.common.compose.navigation.Screen

@Composable
fun BottomNavigationBar(
    screens: List<Screen>,
    currentRoute: String?,
    onClicked: (Screen) -> Unit
) {
    CustomBottomNavigation(
        backgroundColor = MaterialTheme.colors.surface, modifier = Modifier
            .navigationBarsPadding()
    ) {
        screens.forEach { screen ->
            BottomNavigationItem(
                icon = { Icon(screen.icon, null) },
                label = {
                    Text(text = stringResource(id = screen.titleId), maxLines = 1, overflow = TextOverflow.Ellipsis)
                },
                selected = currentRoute == screen.routeId,
                onClick = {
                    if (currentRoute == screen.routeId) return@BottomNavigationItem
                    onClicked(screen)
                }
            )
        }
    }
}