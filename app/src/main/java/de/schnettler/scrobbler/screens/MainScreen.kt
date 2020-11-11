package de.schnettler.scrobbler.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import de.schnettler.database.models.LastFmEntity
import de.schnettler.scrobbler.Screen
import de.schnettler.scrobbler.UIAction
import de.schnettler.scrobbler.UIError
import de.schnettler.scrobbler.util.SessionState
import androidx.compose.runtime.getValue
import de.schnettler.scrobbler.viewmodels.AlbumViewModel
import de.schnettler.scrobbler.viewmodels.ArtistViewModel
import de.schnettler.scrobbler.viewmodels.ChartsViewModel
import de.schnettler.scrobbler.viewmodels.LocalViewModel
import de.schnettler.scrobbler.viewmodels.MainViewModel
import de.schnettler.scrobbler.viewmodels.SearchViewModel
import de.schnettler.scrobbler.viewmodels.TrackViewModel
import de.schnettler.scrobbler.viewmodels.UserViewModel

@Composable
fun MainRouteContent(
    model: MainViewModel,
    navController: NavHostController,
    chartsModel: ChartsViewModel,
    userViewModel: UserViewModel,
    localViewModel: LocalViewModel,
    searchViewModel: SearchViewModel,
    artistViewModel: ArtistViewModel,
    albumViewModel: AlbumViewModel,
    trackViewModel: TrackViewModel,
    actioner: (UIAction) -> Unit,
    errorer: @Composable (UIError) -> Unit,
    modifier: Modifier = Modifier,
) {
    val sessionStatus by model.sessionStatus.observeAsState(SessionState.LoggedIn)

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
        composable(Screen.ArtistDetails.argRoute, arguments = Screen.ArtistDetails.navArgs) { screen ->
            screen.arguments?.getString(Screen.ArtistDetails.args.first().name)?.let {
                artistViewModel.updateKey(LastFmEntity.Artist(it))
                DetailScreen(model = artistViewModel, actioner = actioner, errorer = errorer, modifier = modifier)
            }
        }
        composable(
            Screen.AlbumDetails.argRoute,
            arguments = Screen.AlbumDetails.navArgs
        ) {
            val artist = it.arguments?.getString(Screen.AlbumDetails.args.first().name)
            val album = it.arguments?.getString(Screen.AlbumDetails.args[1].name)

            if (!artist.isNullOrEmpty() && !album.isNullOrEmpty()) {
                albumViewModel.updateKey(LastFmEntity.Album(name = album, artist = artist))
                DetailScreen(model = albumViewModel, actioner = actioner, errorer = errorer, modifier = modifier)
            }
        }
        composable(
            Screen.TrackDetails.argRoute,
            arguments = Screen.TrackDetails.navArgs
        ) {
            val artist = it.arguments?.getString(Screen.TrackDetails.args.first().name)
            val track = it.arguments?.getString(Screen.TrackDetails.args[1].name)

            if (!artist.isNullOrEmpty() && !track.isNullOrEmpty()) {
                trackViewModel.updateKey(LastFmEntity.Track(name = track, artist = artist))
                DetailScreen(model = trackViewModel, actioner = actioner, errorer = errorer, modifier = modifier)
            }
        }
    }
}