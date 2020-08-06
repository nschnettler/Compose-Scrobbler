package de.schnettler.scrobbler.screens

import androidx.compose.Composable
import androidx.compose.getValue
import androidx.ui.animation.Crossfade
import androidx.ui.core.ContextAmbient
import androidx.ui.foundation.Icon
import androidx.ui.foundation.Text
import androidx.ui.livedata.observeAsState
import androidx.ui.material.IconButton
import androidx.ui.material.MaterialTheme
import androidx.ui.material.TopAppBar
import androidx.ui.text.style.TextOverflow
import de.schnettler.database.models.LastFmEntity
import de.schnettler.scrobbler.AppRoute
import de.schnettler.scrobbler.screens.details.DetailScreen
import de.schnettler.scrobbler.util.MenuAction
import de.schnettler.scrobbler.util.SessionState
import de.schnettler.scrobbler.viewmodels.ChartsViewModel
import de.schnettler.scrobbler.viewmodels.DetailViewModel
import de.schnettler.scrobbler.viewmodels.LocalViewModel
import de.schnettler.scrobbler.viewmodels.MainViewModel
import de.schnettler.scrobbler.viewmodels.SearchViewModel
import de.schnettler.scrobbler.viewmodels.UserViewModel
import timber.log.Timber

@Composable
fun ToolBar(currentScreen: AppRoute) {
    TopAppBar(
        title = {
            Text(
                text = currentScreen.title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        actions = {
            currentScreen.menuActions.forEach { menuAction ->
                Timber.d("MenuItem $menuAction")
                IconButton(onClick = {
                    when (menuAction) {
                        is MenuAction.OpenInBrowser -> {
                            menuAction.onClick.invoke((currentScreen as AppRoute.DetailRoute).item)
                        }
                        is MenuAction.Period -> menuAction.onClick.invoke()
                    }
                }) {
                    Icon(menuAction.icon)
                }
            }
        },
        backgroundColor = MaterialTheme.colors.surface
    )
}

@Composable
fun AppContent(
    currentScreen: AppRoute,
    model: MainViewModel,
    chartsModel: ChartsViewModel,
    detailsViewModel: DetailViewModel,
    userViewModel: UserViewModel,
    localViewModel: LocalViewModel,
    searchViewModel: SearchViewModel,
    onListingClicked: (LastFmEntity) -> Unit,
    onTagClicked: (String) -> Unit
) {
    val sessionStatus by model.sessionStatus.observeAsState(SessionState.LoggedOut)

    Crossfade(currentScreen) { screen ->
        when (screen) {
            is AppRoute.ChartRoute -> ChartScreen(
                model = chartsModel,
                onListingSelected = onListingClicked
            )
            is AppRoute.LocalRoute -> LocalScreen(
                localViewModel = localViewModel,
                onListingSelected = onListingClicked
            )
            is AppRoute.ProfileRoute -> {
                when (sessionStatus) {
                    is SessionState.LoggedOut -> LoginScreen(ContextAmbient.current)
                    is SessionState.LoggedIn -> {
                        ProfileScreen(userViewModel, onListingSelected = onListingClicked)
                    }
                }
            }
            is AppRoute.SearchRoute -> SearchScreen(searchViewModel, onListingClicked)
            is AppRoute.DetailRoute -> {
                detailsViewModel.updateEntry(screen.item)
                DetailScreen(
                    model = detailsViewModel,
                    onListingSelected = onListingClicked,
                    onTagClicked = onTagClicked
                )
            }
        }
    }
}