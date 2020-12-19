package de.schnettler.scrobbler.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import de.schnettler.database.models.LastFmEntity
import de.schnettler.scrobbler.ui.common.compose.navigation.Screen
import de.schnettler.scrobbler.ui.charts.ChartScreen
import de.schnettler.scrobbler.ui.common.compose.navigation.UIAction
import de.schnettler.scrobbler.ui.common.compose.navigation.UIError
import de.schnettler.scrobbler.ui.detail.DetailScreen
import de.schnettler.scrobbler.ui.detail.viewmodel.AlbumViewModel
import de.schnettler.scrobbler.ui.detail.viewmodel.ArtistViewModel
import de.schnettler.scrobbler.ui.detail.viewmodel.TrackViewModel
import de.schnettler.scrobbler.ui.profile.ProfileScreen
import de.schnettler.scrobbler.ui.settings.SettingsScreen
import de.schnettler.scrobbler.util.SessionState
import de.schnettler.scrobbler.util.destination
import de.schnettler.scrobbler.util.secondOrNull
import de.schnettler.scrobbler.viewmodels.LocalViewModel
import de.schnettler.scrobbler.viewmodels.MainViewModel
import de.schnettler.scrobbler.viewmodels.SearchViewModel

@Composable
fun MainRouteContent(
    model: MainViewModel,
    navController: NavHostController,
    chartsModel: de.schnettler.scrobbler.ui.charts.ChartsViewModel,
    userViewModel: de.schnettler.scrobbler.ui.profile.UserViewModel,
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
        destination(Screen.Charts) {
            ChartScreen(model = chartsModel, actionHandler = actioner, errorHandler = errorer, modifier = modifier)
        }
        destination(Screen.History) {
            HistoryScreen(
                model = localViewModel, actionHandler = actioner, errorHandler = errorer, modifier = modifier,
                loggedIn = sessionStatus is SessionState.LoggedIn
            )
        }
        destination(Screen.Search) {
            SearchScreen(model = searchViewModel, actionHandler = actioner, errorHandler = errorer, modifier = modifier)
        }
        destination(Screen.Profile) {
            ProfileScreen(model = userViewModel, actionHandler = actioner, errorHandler = errorer, modifier = modifier)
        }
        destination(Screen.Settings) { SettingsScreen(modifier = modifier) }
        destination(Screen.ArtistDetails) { args ->
            args.firstOrNull()?.let {
                artistViewModel.updateKey(LastFmEntity.Artist(it))
                DetailScreen(model = artistViewModel, actioner = actioner, errorer = errorer)
            }
        }
        destination(screen = Screen.AlbumDetails) { args ->
            val artist = args.firstOrNull()
            val album = args.secondOrNull()
            if (!artist.isNullOrEmpty() && !album.isNullOrEmpty()) {
                albumViewModel.updateKey(LastFmEntity.Album(name = album, artist = artist))
                DetailScreen(model = albumViewModel, actioner = actioner, errorer = errorer)
            }
        }
        destination(screen = Screen.TrackDetails) { args ->
            val artist = args.firstOrNull()
            val track = args.secondOrNull()
            if (!artist.isNullOrEmpty() && !track.isNullOrEmpty()) {
                trackViewModel.updateKey(LastFmEntity.Track(name = track, artist = artist))
                DetailScreen(model = trackViewModel, actioner = actioner, errorer = errorer)
            }
        }
    }
}