package de.schnettler.scrobbler.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ContextAmbient
import de.schnettler.scrobbler.AppRoute
import de.schnettler.scrobbler.MainRoute
import de.schnettler.scrobbler.NestedRoute
import de.schnettler.scrobbler.UIAction
import de.schnettler.scrobbler.UIError
import de.schnettler.scrobbler.util.SessionState
import de.schnettler.scrobbler.viewmodels.ChartsViewModel
import de.schnettler.scrobbler.viewmodels.DetailViewModel
import de.schnettler.scrobbler.viewmodels.LocalViewModel
import de.schnettler.scrobbler.viewmodels.MainViewModel
import de.schnettler.scrobbler.viewmodels.SearchViewModel
import de.schnettler.scrobbler.viewmodels.UserViewModel

@Composable
fun MainRouteContent(
    currentScreen: AppRoute,
    model: MainViewModel,
    chartsModel: ChartsViewModel,
    detailsViewModel: DetailViewModel,
    userViewModel: UserViewModel,
    localViewModel: LocalViewModel,
    searchViewModel: SearchViewModel,
    actionHandler: (UIAction) -> Unit,
    errorHandler: @Composable() (UIError) -> Unit,
    modifier: Modifier = Modifier,
) {
    val sessionStatus by model.sessionStatus.observeAsState(SessionState.LoggedIn)

//    Crossfade(currentScreen) { screen ->
        when (val screen = currentScreen) {
            is MainRoute.ChartRoute -> ChartScreen(
                model = chartsModel,
                actionHandler = actionHandler,
                errorHandler = errorHandler,
                modifier = modifier,
            )
            is MainRoute.LocalRoute -> LocalScreen(
                localViewModel = localViewModel,
                actionHandler = actionHandler,
                errorHandler = errorHandler,
                modifier = modifier,
            )
            is MainRoute.ProfileRoute -> {
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
            is MainRoute.SearchRoute -> SearchScreen(searchViewModel, actionHandler, errorHandler, modifier)
            is MainRoute.SettingsRoute -> SettingsScreen(modifier = modifier)
            is NestedRoute.DetailRoute -> {
                detailsViewModel.updateEntry(screen.item)
                DetailScreen(
                    model = detailsViewModel,
                    actionHandler = actionHandler,
                    errorHandler = errorHandler,
                    modifier = modifier,
                )
            }
        }
//    }
}