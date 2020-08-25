package de.schnettler.scrobbler.screens

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Icon
import androidx.compose.foundation.Text
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ContextAmbient
import androidx.compose.ui.text.style.TextOverflow
import de.schnettler.scrobbler.AppRoute
import de.schnettler.scrobbler.UIAction
import de.schnettler.scrobbler.UIError
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
    actionHandler: (UIAction) -> Unit,
    errorHandler: @Composable (UIError) -> Unit,
    modifier: Modifier = Modifier,
) {
    val sessionStatus by model.sessionStatus.observeAsState(SessionState.LoggedOut)

    Crossfade(currentScreen) { screen ->
        when (screen) {
            is AppRoute.ChartRoute -> ChartScreen(
                model = chartsModel,
                actionHandler = actionHandler,
                errorHandler = errorHandler,
                modifier = modifier,
            )
            is AppRoute.LocalRoute -> LocalScreen(
                localViewModel = localViewModel,
                actionHandler = actionHandler,
                errorHandler = errorHandler,
                modifier = modifier,
            )
            is AppRoute.ProfileRoute -> {
                when (sessionStatus) {
                    is SessionState.LoggedOut -> LoginScreen(ContextAmbient.current)
                    is SessionState.LoggedIn -> {
                        ProfileScreen(
                            model = userViewModel,
                            actionHandler = actionHandler,
                            errorHandler = errorHandler,
                            modifier = modifier,
                        )
                    }
                }
            }
            is AppRoute.SearchRoute -> SearchScreen(searchViewModel, actionHandler, errorHandler, modifier)
            is AppRoute.DetailRoute -> {
                detailsViewModel.updateEntry(screen.item)
                DetailScreen(
                    model = detailsViewModel,
                    actionHandler = actionHandler,
                    errorHandler = errorHandler,
                    modifier = modifier,
                )
            }
            is AppRoute.SettingsRoute -> PreferenceScreen()
        }
    }
}