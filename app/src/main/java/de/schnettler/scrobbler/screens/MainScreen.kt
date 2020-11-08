package de.schnettler.scrobbler.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import de.schnettler.database.models.LastFmEntity
import de.schnettler.scrobbler.Screen
import de.schnettler.scrobbler.Screen.AlbumDetails
import de.schnettler.scrobbler.Screen.ArtistDetails
import de.schnettler.scrobbler.Screen.TrackDetails
import de.schnettler.scrobbler.UIAction
import de.schnettler.scrobbler.UIError
import de.schnettler.scrobbler.viewmodels.AlbumViewModel
import de.schnettler.scrobbler.viewmodels.ArtistViewModel
import de.schnettler.scrobbler.viewmodels.ChartsViewModel
import de.schnettler.scrobbler.viewmodels.LocalViewModel
import de.schnettler.scrobbler.viewmodels.SearchViewModel
import de.schnettler.scrobbler.viewmodels.TrackViewModel
import de.schnettler.scrobbler.viewmodels.UserViewModel

@Composable
fun MainRouteContent(
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
    NavHost(navController = navController, startDestination = Screen.History.fullRoute) {
        composable(Screen.Charts.routeId) {
            ChartScreen(model = chartsModel, actionHandler = actioner, errorHandler = errorer, modifier = modifier)
        }
        composable(Screen.History.fullRoute) {
            HistoryScreen(
                model = localViewModel,
                actionHandler = actioner,
                errorHandler = errorer,
                modifier = modifier,
                loggedIn = sessionStatus is SessionState.LoggedIn
            )
        }
        composable(Screen.Search.fullRoute) {
            SearchScreen(model = searchViewModel, actionHandler = actioner, errorHandler = errorer, modifier = modifier)
        }
        composable(Screen.Profile.fullRoute) {
            ProfileScreen(model = userViewModel, actionHandler = actioner, errorHandler = errorer, modifier = modifier)
        }
        composable(Screen.Settings.fullRoute) {
            SettingsScreen(modifier = modifier)
        }
        composable(
            ArtistDetails.fullRoute,
            arguments = ArtistDetails.navArgs,
        ) {
            val artistName = it.arguments?.getString(ArtistDetails.args.first().name)
            if (!artistName.isNullOrEmpty()) { artistViewModel.updateKey(LastFmEntity.Artist(artistName)) }
            DetailScreen(model = artistViewModel, actioner = actioner, errorer = errorer, modifier = modifier)
        }
        composable(
            AlbumDetails.fullRoute,
            arguments = AlbumDetails.navArgs
        ) {
            val artist = it.arguments?.getString(AlbumDetails.args.first().name)
            val album = it.arguments?.getString(AlbumDetails.args[1].name)
            if (!artist.isNullOrEmpty() && !album.isNullOrEmpty()) {
                albumViewModel.updateKey(LastFmEntity.Album(name = album, artist = artist))
            }
            DetailScreen(model = albumViewModel, actioner = actioner, errorer = errorer, modifier = modifier)
        }
        composable(
            TrackDetails.fullRoute,
            arguments = TrackDetails.navArgs
        ) {
            val artist = it.arguments?.getString(TrackDetails.args.first().name)
            val track = it.arguments?.getString(TrackDetails.args[1].name)
            if (!artist.isNullOrEmpty() && !track.isNullOrEmpty()) {
                trackViewModel.updateKey(LastFmEntity.Track(name = track, artist = artist))
            }
            DetailScreen(model = trackViewModel, actioner = actioner, errorer = errorer, modifier = modifier)
        }
    }
}