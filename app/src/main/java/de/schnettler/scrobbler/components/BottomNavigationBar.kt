package de.schnettler.scrobbler.components

import androidx.compose.foundation.Text
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavHostController
import androidx.navigation.compose.KEY_ROUTE
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigate
import de.schnettler.scrobbler.Screen
import de.schnettler.scrobbler.util.navigationBarsPadding

@Composable
fun BottomNavigationBar(
    screens: List<Screen>,
    navController: NavHostController
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.arguments?.getString(KEY_ROUTE)

    if (Screen.mainRoutes.map { it.routeId }.contains(currentRoute)) {
        CustomBottomNavigation(
            backgroundColor = MaterialTheme.colors.surface, modifier = Modifier
                .navigationBarsPadding()
        ) {
            screens.forEach { screen ->
                BottomNavigationItem(
                    icon = { Icon(screen.icon) },
                    label = {
                        Text(text = stringResource(id = screen.titleId), maxLines = 1, overflow = TextOverflow.Ellipsis)
                    },
                    selected = currentRoute == screen.routeId,
                    onClick = {
                        navController.popBackStack(navController.graph.startDestination, false)
                        if (currentRoute != screen.routeId) {
                            navController.navigate(screen.routeId)
                        }
                    }
                )
            }
        }
    }
}