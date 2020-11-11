package de.schnettler.scrobbler

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.SnackbarResult
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedTask
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.setContent
import androidx.core.view.WindowCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigate
import androidx.navigation.compose.rememberNavController
import com.tfcporciuncula.flow.FlowSharedPreferences
import dagger.hilt.android.AndroidEntryPoint
import de.schnettler.composepreferences.ProvidePreferences
import de.schnettler.database.models.LastFmEntity
import de.schnettler.scrobbler.components.BottomNavigationBar
import de.schnettler.scrobbler.screens.MainRouteContent
import de.schnettler.scrobbler.theme.AppTheme
import de.schnettler.scrobbler.util.REDIRECT_URL
import de.schnettler.scrobbler.util.RefreshableUiState
import de.schnettler.scrobbler.util.openCustomTab
import de.schnettler.scrobbler.util.openNotificationListenerSettings
import de.schnettler.scrobbler.util.route
import de.schnettler.scrobbler.viewmodels.AlbumViewModel
import de.schnettler.scrobbler.viewmodels.ArtistViewModel
import de.schnettler.scrobbler.viewmodels.ChartsViewModel
import de.schnettler.scrobbler.viewmodels.DetailViewModel
import de.schnettler.scrobbler.viewmodels.LocalViewModel
import de.schnettler.scrobbler.viewmodels.MainViewModel
import de.schnettler.scrobbler.viewmodels.SearchViewModel
import de.schnettler.scrobbler.viewmodels.TrackViewModel
import de.schnettler.scrobbler.viewmodels.UserViewModel
import dev.chrisbanes.accompanist.insets.ProvideWindowInsets
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    // ViewModels
    private val model: MainViewModel by viewModels()
    private val chartsModel: ChartsViewModel by viewModels()
    private val detailsViewModel: DetailViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()
    private val localViewModel: LocalViewModel by viewModels()
    private val searchViewModel: SearchViewModel by viewModels()
    private val artistViewModel: ArtistViewModel by viewModels()
    private val albumViewModel: AlbumViewModel by viewModels()
    private val trackViewModel: TrackViewModel by viewModels()

    private lateinit var onListingClicked: (LastFmEntity) -> Unit

    private val mainScreens = listOf(
        Screen.Charts,
        Screen.History,
        Screen.Search,
        Screen.Profile,
        Screen.Settings
    )

    @Inject
    lateinit var sharedPrefs: FlowSharedPreferences

    @OptIn(ExperimentalMaterialApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            ProvidePreferences(sharedPreferences = sharedPrefs) {
                AppTheme {
                    ProvideWindowInsets {
                        val navController = rememberNavController()
                        val snackHost = remember { SnackbarHostState() }
                        onListingClicked = {
                            navController.navigate(when (it) {
                                is LastFmEntity.Artist -> Screen.ArtistDetails.withArg(it.name)
                                is LastFmEntity.Album -> Screen.AlbumDetails.withArgs(listOf(it.artist, it.name))
                                is LastFmEntity.Track -> Screen.TrackDetails.withArgs(listOf(it.artist, it.name))
                            })
                        }

                        val navBackStackEntry by navController.currentBackStackEntryAsState()
                        Scaffold(
                            scaffoldState = rememberScaffoldState(snackbarHostState = snackHost),
                            bottomBar = {
                                if (mainScreens.map { it.routeId }.contains(navBackStackEntry?.route())) {
                                    BottomNavigationBar(
                                        currentRoute = navBackStackEntry?.route(),
                                        screens = mainScreens,
                                    ) {
                                        navController.popBackStack(navController.graph.startDestination, false)
                                        navController.navigate(it.routeId)
                                    }
                                }
                            }
                        ) {
                            Content(controller = navController, host = snackHost, innerPadding = it)
                        }
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    private fun Content(controller: NavHostController, host: SnackbarHostState, innerPadding: PaddingValues) {
        MainRouteContent(
            model = model,
            navController = controller,
            chartsModel = chartsModel,
            userViewModel = userViewModel,
            localViewModel = localViewModel,
            searchViewModel = searchViewModel,
            artistViewModel = artistViewModel,
            albumViewModel = albumViewModel,
            trackViewModel = trackViewModel,
            actioner = ::handleAction,
            errorer = { error -> handleError(host = host, error = error) },
            modifier = Modifier.padding(innerPadding)
        )
    }

    private fun handleAction(action: UIAction) {
        when (action) {
            is UIAction.ListingSelected -> onListingClicked(action.listing)
            is UIAction.TagSelected -> openCustomTab("https://www.last.fm/tag/${action.id}")
            is UIAction.TrackLiked -> detailsViewModel.onToggleLoveTrackClicked(action.track, action.info)
            is UIAction.NavigateUp -> onBackPressed()
            is UIAction.OpenInBrowser -> openCustomTab(action.url)
            is UIAction.ShowTimePeriodDialog -> userViewModel.showDialog(true)
            is UIAction.OpenNotificationListenerSettings -> openNotificationListenerSettings()
        }
    }

    @ExperimentalMaterialApi
    @Composable
    fun handleError(host: SnackbarHostState, error: UIError) {
        when (error) {
            is UIError.ShowErrorSnackbar -> showErrorSnackbar(host = host, error = error)
        }
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun showErrorSnackbar(host: SnackbarHostState, error: UIError.ShowErrorSnackbar) {
        if (error.state is RefreshableUiState.Error) {
            LaunchedTask {
                val result = host.showSnackbar(
                    message = error.state.errorMessage
                        ?: error.state.exception?.message
                        ?: error.fallbackMessage,
                    actionLabel = error.actionMessage
                )
                when (result) {
                    SnackbarResult.ActionPerformed -> error.onAction()
                    SnackbarResult.Dismissed -> error.onDismiss()
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if ("${intent?.data?.scheme}://${intent?.data?.host}" == REDIRECT_URL) {
            // AuthResponse
            intent?.data?.getQueryParameter("token")?.let { token ->
                Timber.i("TOKEN: $token")
                model.onTokenReceived(token)
            }
        }
    }
}