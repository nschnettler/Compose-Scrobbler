package de.schnettler.scrobbler

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltNavGraphViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import de.schnettler.scrobbler.compose.navigation.Screen
import de.schnettler.scrobbler.compose.navigation.UIAction
import de.schnettler.scrobbler.compose.navigation.UIError
import de.schnettler.scrobbler.core.model.LastFmEntity
import de.schnettler.scrobbler.model.SessionState
import de.schnettler.scrobbler.search.ui.SearchScreen
import de.schnettler.scrobbler.search.ui.SearchViewModel
import de.schnettler.scrobbler.ui.charts.ChartScreen
import de.schnettler.scrobbler.ui.charts.ChartViewModel
import de.schnettler.scrobbler.ui.charts.ChartViewModelImpl
import de.schnettler.scrobbler.ui.detail.DetailScreen
import de.schnettler.scrobbler.ui.detail.viewmodel.AlbumViewModel
import de.schnettler.scrobbler.ui.detail.viewmodel.ArtistViewModel
import de.schnettler.scrobbler.ui.detail.viewmodel.TrackViewModel
import de.schnettler.scrobbler.ui.history.HistoryScreen
import de.schnettler.scrobbler.ui.history.LocalViewModel
import de.schnettler.scrobbler.ui.profile.ProfileScreen
import de.schnettler.scrobbler.ui.profile.ProfileViewModel
import de.schnettler.scrobbler.ui.profile.ProfileViewModelImpl
import de.schnettler.scrobbler.ui.settings.SettingsScreen
import de.schnettler.scrobbler.util.destination
import de.schnettler.scrobbler.util.secondOrNull

@Composable
fun MainRouteContent(
    model: MainViewModel,
    navController: NavHostController,
    actioner: (UIAction) -> Unit,
    errorer: @Composable (UIError) -> Unit,
    modifier: Modifier = Modifier,
) {
    val sessionStatus by model.sessionStatus.observeAsState(SessionState.LoggedIn)

    NavHost(navController = navController, startDestination = Screen.History.routeId) {
        destination(Screen.Charts) {
            val viewModel: ChartViewModel = hiltNavGraphViewModel<ChartViewModelImpl>()
            ChartScreen(viewModel = viewModel, actionHandler = actioner, errorHandler = errorer, modifier = modifier)
        }
        destination(Screen.History) {
            val viewModel: LocalViewModel = hiltNavGraphViewModel<LocalViewModel>()
            HistoryScreen(
                viewModel = viewModel, actionHandler = actioner, errorHandler = errorer, modifier = modifier,
                loggedIn = sessionStatus is SessionState.LoggedIn
            )
        }
        destination(Screen.Search) {
            val viewModel: SearchViewModel = hiltNavGraphViewModel<SearchViewModel>()
            SearchScreen(viewModel = viewModel, actionHandler = actioner, errorHandler = errorer, modifier = modifier)
        }
        destination(Screen.Profile) {
            val viewModel: ProfileViewModel = hiltNavGraphViewModel<ProfileViewModelImpl>()
            ProfileScreen(viewModel = viewModel, actionHandler = actioner, errorHandler = errorer, modifier = modifier)
        }
        destination(Screen.Settings) { SettingsScreen(modifier = modifier) }
        destination(Screen.ArtistDetails) { args ->
            val viewModel: ArtistViewModel = hiltNavGraphViewModel<ArtistViewModel>()
            args.firstOrNull()?.let {
                viewModel.updateKey(LastFmEntity.Artist(it))
                DetailScreen(viewModel = viewModel, actioner = actioner, errorer = errorer)
            }
        }
        destination(screen = Screen.AlbumDetails) { args ->
            val viewModel: AlbumViewModel = hiltNavGraphViewModel<AlbumViewModel>()
            val artist = args.firstOrNull()
            val album = args.secondOrNull()
            if (!artist.isNullOrEmpty() && !album.isNullOrEmpty()) {
                viewModel.updateKey(LastFmEntity.Album(name = album, artist = artist))
                DetailScreen(viewModel = viewModel, actioner = actioner, errorer = errorer)
            }
        }
        destination(screen = Screen.TrackDetails) { args ->
            val viewModel: TrackViewModel = hiltNavGraphViewModel<TrackViewModel>()
            val artist = args.firstOrNull()
            val track = args.secondOrNull()
            if (!artist.isNullOrEmpty() && !track.isNullOrEmpty()) {
                viewModel.updateKey(LastFmEntity.Track(name = track, artist = artist))
                DetailScreen(viewModel = viewModel, actioner = actioner, errorer = errorer)
            }
        }
    }
}