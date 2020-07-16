package de.schnettler.scrobbler

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.Composable
import androidx.compose.getValue
import androidx.ui.animation.Crossfade
import androidx.ui.core.setContent
import androidx.ui.foundation.Icon
import androidx.ui.foundation.Text
import androidx.ui.foundation.isSystemInDarkTheme
import androidx.ui.graphics.Color
import androidx.ui.livedata.observeAsState
import androidx.ui.material.*
import androidx.ui.res.vectorResource
import androidx.ui.text.style.TextOverflow
import com.koduok.compose.navigation.Router
import com.koduok.compose.navigation.core.backStackController
import dagger.hilt.android.AndroidEntryPoint
import de.schnettler.database.models.CommonEntity
import de.schnettler.database.models.LastFmEntity
import de.schnettler.database.models.LastFmStatsEntity
import de.schnettler.database.models.LocalTrack
import de.schnettler.scrobbler.components.BottomNavigationBar
import de.schnettler.scrobbler.screens.*
import de.schnettler.scrobbler.screens.details.DetailScreen
import de.schnettler.scrobbler.util.MenuAction
import de.schnettler.scrobbler.util.SessionState
import de.schnettler.scrobbler.util.openUrlInCustomTab
import de.schnettler.scrobbler.viewmodels.*
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    //ViewModels
    private val model: MainViewModel by viewModels()
    private val chartsModel: ChartsViewModel by viewModels()
    private val detailsViewModel: DetailViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()
    private val historyViewModel: HistoryViewModel by viewModels()
    private val localViewModel: LocalViewModel by viewModels()

    private val onOpenInBrowser: (LastFmEntity) -> Unit = {
        openUrlInCustomTab(it.url)
    }

    private val onTagClicked: (String) -> Unit = {tag ->
        val url = "https://www.last.fm/tag/$tag"
        openUrlInCustomTab(url)
    }

    private lateinit var onListingClicked: (CommonEntity) -> Unit

    private val bottomNavDestinations = listOf(
            AppRoute.ChartRoute,
            AppRoute.LocalRoute,
            AppRoute.HistoryRoute,
            AppRoute.ProfileRoute(onFilterClicked = {
                userViewModel.showDialog(true)
            })
    )

    private val startScreen: AppRoute = AppRoute.LocalRoute

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val colorPalette = if (isSystemInDarkTheme()) {
                darkThemeColors
            } else {
                lightThemeColors
            }
            MaterialTheme(colors = colorPalette) {
                Router(start = startScreen) { currentRoute ->
                    onListingClicked = {
                        when(it) {
                            is LastFmStatsEntity -> this.push(AppRoute.DetailRoute(item = it, onOpenInBrowser = onOpenInBrowser))
                            is LocalTrack -> Timber.d("Scrobble plz!!!")
                        }

                    }

                    Scaffold(
                        topBar = {
                            TopAppBar(
                                title = { Text(text = currentRoute.data.title, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                                actions = {
                                    currentRoute.data.menuActions.forEach {menuAction ->
                                        Timber.d("MenuItem $menuAction")
                                        IconButton(onClick = {
                                            when(menuAction) {
                                                is MenuAction.OpenInBrowser -> {
                                                    menuAction.onClick.invoke((currentRoute.data as AppRoute.DetailRoute).item)
                                                }
                                                is MenuAction.Period -> menuAction.onClick.invoke()
                                            }
                                        }) {
                                            Icon(vectorResource(id = menuAction.icon))
                                        }
                                    }
                                },
                                backgroundColor = MaterialTheme.colors.surface
                            )
                        },
                        bodyContent = {
                            AppContent(currentRoute.data)
                        },
                        bottomBar = {
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
                    DetailScreen(model = detailsViewModel, onListingSelected = onListingClicked, onTagClicked = onTagClicked)
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

val lightThemeColors  = lightColorPalette(
    primary = Color(0xFF7E8ACD),
    primaryVariant = Color(0xFF7E8ACD),
    secondary = Color(0xFF7E8ACD),
    secondaryVariant = Color(0x7E8ACD),
    onPrimary = Color.Black
)
val darkThemeColors = darkColorPalette(
    primary = Color(0xFF7E8ACD),
    primaryVariant = Color(0xFF7E8ACD),
    secondary = Color(0xFF7E8ACD),
    background = Color(0xFF202030),
    surface = Color(0xFF202030)
)