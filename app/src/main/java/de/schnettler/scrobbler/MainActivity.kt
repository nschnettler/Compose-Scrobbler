package de.schnettler.scrobbler

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.*
import androidx.ui.animation.Crossfade
import androidx.ui.core.setContent
import androidx.ui.foundation.Icon
import androidx.ui.foundation.Text
import androidx.ui.livedata.observeAsState
import androidx.ui.material.*
import androidx.ui.res.vectorResource
import com.koduok.compose.navigation.Router
import com.koduok.compose.navigation.core.backStackController
import dagger.hilt.android.AndroidEntryPoint
import de.schnettler.database.models.ListingMin
import de.schnettler.scrobbler.components.BottomNavigationBar
import de.schnettler.scrobbler.screens.*
import de.schnettler.scrobbler.screens.details.DetailScreen
import de.schnettler.scrobbler.util.MenuAction
import de.schnettler.scrobbler.util.SessionState
import de.schnettler.scrobbler.util.onOpenInBrowserClicked
import de.schnettler.scrobbler.viewmodels.*
import dev.chrisbanes.accompanist.mdctheme.MaterialThemeFromMdcTheme
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import timber.log.Timber

@FlowPreview
@ExperimentalCoroutinesApi
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    //ViewModels
    private val model: MainViewModel by viewModels()
    private val chartsModel: ChartsViewModel by viewModels()
    private val detailsViewModel: DetailViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()
    private val historyViewModel: HistoryViewModel by viewModels()
    private val localViewModel: LocalViewModel by viewModels()

    private val onOpenInBrowser: (ListingMin) -> Unit = {
        onOpenInBrowserClicked(it, this)
    }

    private lateinit var onListingClicked: (ListingMin) -> Unit

    private val bottomNavDestinations = listOf(
            AppRoute.ChartRoute,
            AppRoute.LocalRoute,
            AppRoute.HistoryRoute(onRefreshClicked = {
                historyViewModel.refreshHistory()
            }),
            AppRoute.ProfileRoute(onFilterClicked = {
                userViewModel.showDialog(true)
            })
    )

    private val startScreen: AppRoute = AppRoute.LocalRoute

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialThemeFromMdcTheme  {
                Router(start = startScreen) { currentRoute ->
                    onListingClicked = {
                        this.push(AppRoute.DetailRoute(item = it, onOpenInBrowser = onOpenInBrowser))
                    }

                    Scaffold(
                        topAppBar = {
                            TopAppBar(
                                title = { Text(text = currentRoute.data.title) },
                                actions = {
                                    currentRoute.data.menuActions.forEach {menuAction ->
                                        Timber.d("MenuItem $menuAction")
                                        IconButton(onClick = {
                                            when(menuAction) {
                                                is MenuAction.OpenInBrowser -> {
                                                    menuAction.onClick.invoke((currentRoute.data as AppRoute.DetailRoute).item)
                                                }
                                                is MenuAction.Period -> menuAction.onClick.invoke()
                                                is MenuAction.Refresh -> menuAction.onClick.invoke()
                                            }
                                        }) {
                                            Icon(vectorResource(id = menuAction.icon))
                                        }
                                    }
                                }
                            )
                        },
                        bodyContent = {
                            AppContent(currentRoute.data)
                        },
                        bottomAppBar = {
                            BottomNavigationBar(
                                    items = bottomNavDestinations,
                                    currentScreen = currentRoute.data
                            ) { newScreen ->
                                replace(newScreen)
                            }
                        }
                    )
                }
            }
        }
    }

    @Composable
    private fun AppContent(currentScreen: AppRoute) {
        val sessionStatus by model.sessionStatus.observeAsState(SessionState.LoggedOut)

        Crossfade(currentScreen) { screen ->
            when(screen) {
                is AppRoute.ChartRoute -> ChartScreen(model = chartsModel, onListingSelected = onListingClicked)
                is AppRoute.HistoryRoute ->  {
                    when(sessionStatus) {
                        is SessionState.LoggedOut -> LoginScreen(context = this)
                        is SessionState.LoggedIn -> HistoryScreen(historyViewModel, onListingSelected = onListingClicked)
                    }
                }
                is AppRoute.LocalRoute -> LocalScreen(localViewModel = localViewModel)
                is AppRoute.ProfileRoute -> {
                    when(sessionStatus) {
                        is SessionState.LoggedOut -> LoginScreen(context = this)
                        is SessionState.LoggedIn -> {
                            ProfileScreen(userViewModel, onListingSelected = onListingClicked)
                        }
                    }
                }
                is AppRoute.DetailRoute -> {
                    detailsViewModel.updateEntry(screen.item)
                    DetailScreen(model = detailsViewModel, onListingSelected = onListingClicked)
                }
            }
        }
    }

    override fun onBackPressed() {
        if (!backStackController.pop()) super.onBackPressed()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if ("${intent?.data?.scheme}://${intent?.data?.host}" == REDIRECT_URL) {
            //AuthResponse
            intent?.data?.getQueryParameter("token")?.let {token ->
                Timber.i("TOKEN: $token")
                model.onTokenReceived(token)
            }
        }
    }
}

const val AUTH_ENDPOINT = "https://www.last.fm/api/auth/"
const val REDIRECT_URL = "de.schnettler.scrobble://auth"