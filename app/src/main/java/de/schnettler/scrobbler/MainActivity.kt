package de.schnettler.scrobbler

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigate
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import de.schnettler.database.models.LastFmEntity
import de.schnettler.datastore.compose.ProvideDataStoreManager
import de.schnettler.datastore.manager.DataStoreManager
import de.schnettler.scrobbler.ui.charts.ChartsViewModel
import de.schnettler.scrobbler.ui.common.compose.RefreshableUiState
import de.schnettler.scrobbler.ui.common.compose.navigation.Screen
import de.schnettler.scrobbler.ui.common.compose.navigation.UIAction
import de.schnettler.scrobbler.ui.common.compose.navigation.UIError
import de.schnettler.scrobbler.ui.common.compose.theme.AppTheme
import de.schnettler.scrobbler.ui.common.compose.widget.BottomNavigationBar
import de.schnettler.scrobbler.ui.common.util.REDIRECT_URL
import de.schnettler.scrobbler.ui.detail.DetailViewModel
import de.schnettler.scrobbler.ui.detail.viewmodel.AlbumViewModel
import de.schnettler.scrobbler.ui.detail.viewmodel.ArtistViewModel
import de.schnettler.scrobbler.ui.detail.viewmodel.TrackViewModel
import de.schnettler.scrobbler.ui.history.LocalViewModel
import de.schnettler.scrobbler.ui.profile.UserViewModel
import de.schnettler.scrobbler.ui.search.SearchViewModel
import de.schnettler.scrobbler.util.openCustomTab
import de.schnettler.scrobbler.util.openNotificationListenerSettings
import de.schnettler.scrobbler.util.route
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
    lateinit var dataStoreManager: DataStoreManager

    @OptIn(ExperimentalMaterialApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            ProvideDataStoreManager(dataStoreManager = dataStoreManager) {
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
                                // navBackStackEntry == null is needed because otherwise innerPadding stays zero
                                if (mainScreens.map { it.routeId }
                                        .contains(navBackStackEntry?.route()) || navBackStackEntry == null) {
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
            errorer = { error ->
                when (error) {
                    is UIError.ShowErrorSnackbar -> ErrorSnackbar(host = host, error = error)
                    is UIError.ScrobbleSubmissionResult -> InfoSnackbar(host = host, error = error)
                }
            },
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

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun ErrorSnackbar(host: SnackbarHostState, error: UIError.ShowErrorSnackbar) {
        val state = error.state
        if (state is RefreshableUiState.Error) {
            LaunchedEffect(state) {
                val result = host.showSnackbar(
                    message = state.errorMessage
                        ?: state.exception?.message
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

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun InfoSnackbar(host: SnackbarHostState, error: UIError.ScrobbleSubmissionResult) {
        LaunchedEffect(error) {
            val result = host.showSnackbar(
                message = "Result: ${error.accepted} accepted, ${error.ignored} rejected.",
                actionLabel = error.actionMessage
            )
            when (result) {
                SnackbarResult.ActionPerformed -> error.onAction()
                SnackbarResult.Dismissed -> error.onDismiss()
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