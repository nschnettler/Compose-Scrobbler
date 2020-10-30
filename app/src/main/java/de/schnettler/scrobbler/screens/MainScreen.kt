package de.schnettler.scrobbler.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navArgument
import de.schnettler.scrobbler.Screen
import de.schnettler.scrobbler.UIAction
import de.schnettler.scrobbler.UIError
import de.schnettler.scrobbler.viewmodels.ChartsViewModel
import de.schnettler.scrobbler.viewmodels.DetailViewModel
import de.schnettler.scrobbler.viewmodels.LocalViewModel
import de.schnettler.scrobbler.viewmodels.MainViewModel
import de.schnettler.scrobbler.viewmodels.SearchViewModel
import de.schnettler.scrobbler.viewmodels.UserViewModel

@Composable
fun MainRouteContent(
    navController: NavHostController,
    model: MainViewModel,
    chartsModel: ChartsViewModel,
    detailsViewModel: DetailViewModel,
    userViewModel: UserViewModel,
    localViewModel: LocalViewModel,
    searchViewModel: SearchViewModel,
    actioner: (UIAction) -> Unit,
    errorer: @Composable (UIError) -> Unit,
    modifier: Modifier = Modifier,
) {
    NavHost(navController = navController, startDestination = Screen.History.routeId) {
        composable(Screen.Charts.routeId) {
            ChartScreen(model = chartsModel, actionHandler = actioner, errorHandler = errorer, modifier = modifier)
        }
        composable(Screen.History.routeId) {
            HistoryScreen(
                model = localViewModel,
                actionHandler = actioner,
                errorHandler = errorer,
                modifier = modifier,
                loggedIn = sessionStatus is SessionState.LoggedIn
            )
        }
        composable(Screen.Search.routeId) {
            SearchScreen(model = searchViewModel, actionHandler = actioner, errorHandler = errorer, modifier = modifier)
        }
        composable(Screen.Profile.routeId) {
            ProfileScreen(model = userViewModel, actionHandler = actioner, errorHandler = errorer, modifier = modifier)
        }
        composable(Screen.Settings.routeId) {
            SettingsScreen(modifier = modifier)
        }
        composable(Screen.detailsRoute, arguments = listOf(navArgument("itemId") { defaultValue = "me" })) {
            val argument = it.arguments?.getString("itemId")
//            detailsViewModel.updateEntry(screen.item)
        }
    }
}