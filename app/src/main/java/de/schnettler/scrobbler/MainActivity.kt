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
import androidx.compose.runtime.launchInComposition
import androidx.compose.runtime.remember
import androidx.compose.runtime.savedinstancestate.rememberSavedInstanceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.setContent
import androidx.core.view.WindowCompat
import com.tfcporciuncula.flow.FlowSharedPreferences
import dagger.hilt.android.AndroidEntryPoint
import de.schnettler.composepreferences.ProvidePreferences
import de.schnettler.database.models.LastFmEntity
import de.schnettler.scrobbler.components.BottomNavigationBar
import de.schnettler.scrobbler.screens.MainRouteContent
import de.schnettler.scrobbler.screens.ToolBar
import de.schnettler.scrobbler.theme.AppTheme
import de.schnettler.scrobbler.util.Navigator
import de.schnettler.scrobbler.util.ProvideDisplayInsets
import de.schnettler.scrobbler.util.REDIRECT_URL
import de.schnettler.scrobbler.util.RefreshableUiState
import de.schnettler.scrobbler.util.openUrlInCustomTab
import de.schnettler.scrobbler.viewmodels.ChartsViewModel
import de.schnettler.scrobbler.viewmodels.DetailViewModel
import de.schnettler.scrobbler.viewmodels.LocalViewModel
import de.schnettler.scrobbler.viewmodels.MainViewModel
import de.schnettler.scrobbler.viewmodels.SearchViewModel
import de.schnettler.scrobbler.viewmodels.UserViewModel
import javax.inject.Inject
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    // ViewModels
    private val model: MainViewModel by viewModels()
    private val chartsModel: ChartsViewModel by viewModels()
    private val detailsViewModel: DetailViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()
    private val localViewModel: LocalViewModel by viewModels()
    private val searchViewModel: SearchViewModel by viewModels()

    private lateinit var onListingClicked: (LastFmEntity) -> Unit

    private val bottomNavDestinations = listOf(
        MainRoute.ChartRoute,
        MainRoute.LocalRoute,
        MainRoute.SearchRoute,
        MainRoute.ProfileRoute,
        MainRoute.SettingsRoute
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
                    ProvideDisplayInsets {
                        val navigator: Navigator<AppRoute> = rememberSavedInstanceState(
                            saver = Navigator.saver(onBackPressedDispatcher)
                        ) {
                            Navigator(MainRoute.LocalRoute, onBackPressedDispatcher)
                        }
                        val snackHost = remember { SnackbarHostState() }

//                        Crossfade(current = navigator.current) { screen ->
                            onListingClicked = {
                                navigator.navigate(NestedRoute.DetailRoute(it))
                            }
                            when (val screen = navigator.current) {
                                is NestedRoute -> {
                                    Scaffold(
                                        scaffoldState = rememberScaffoldState(snackbarHostState = snackHost),
                                        bodyContent = {
                                            Content(screen = screen, host = snackHost, innerPadding = it)
                                        },
                                    )
                                }
                                is MainRoute -> {
                                    Scaffold(
                                        scaffoldState = rememberScaffoldState(snackbarHostState = snackHost),
                                        topBar = { ToolBar(currentScreen = screen, actionHandler = ::handleAction) },
                                        bodyContent = {
                                            Content(screen = screen, host = snackHost, innerPadding = it)
                                        },
                                        bottomBar = {
                                            BottomNavigationBar(
                                                items = bottomNavDestinations,
                                                currentScreen = screen
                                            ) { newScreen -> navigator.replace(newScreen as MainRoute) }
                                        }
                                    )
                                }
                            }
//                        }
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    private fun Content(screen: AppRoute, host: SnackbarHostState, innerPadding: PaddingValues) {
        MainRouteContent(
            currentScreen = screen,
            model = model,
            chartsModel = chartsModel,
            userViewModel = userViewModel,
            localViewModel = localViewModel,
            searchViewModel = searchViewModel,
            detailsViewModel = detailsViewModel,
            actionHandler = ::handleAction,
            errorHandler = { error ->
                handleError(host = host, error = error)
            },
            modifier = Modifier.padding(innerPadding)
        )
    }

    private fun handleAction(action: UIAction) {
        when (action) {
            is UIAction.ListingSelected -> onListingClicked(action.listing)
            is UIAction.TagSelected -> openUrlInCustomTab("https://www.last.fm/tag/${action.id}")
            is UIAction.TrackLiked -> detailsViewModel.onToggleLoveTrackClicked(action.track, action.info)
            is UIAction.NavigateUp -> onBackPressed()
            is UIAction.OpenInBrowser -> openUrlInCustomTab(action.url)
            is UIAction.ShowTimePeriodDialog -> userViewModel.showDialog(true)
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
            launchInComposition {
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