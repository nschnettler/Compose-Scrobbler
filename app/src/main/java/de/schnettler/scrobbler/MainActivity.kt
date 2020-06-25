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
import com.koduok.compose.navigation.core.BackStack
import com.koduok.compose.navigation.core.backStackController
import dagger.hilt.android.AndroidEntryPoint
import de.schnettler.common.TimePeriod
import de.schnettler.database.AppDatabase
import de.schnettler.database.models.ListingMin
import de.schnettler.database.provideDatabase
import de.schnettler.scrobbler.components.BottomNavigationBar
import de.schnettler.scrobbler.screens.*
import de.schnettler.scrobbler.screens.details.DetailScreen
import de.schnettler.scrobbler.util.MenuAction
import de.schnettler.scrobbler.util.SessionStatus
import de.schnettler.scrobbler.util.onOpenInBrowserClicked
import de.schnettler.scrobbler.viewmodels.*
import dev.chrisbanes.accompanist.mdctheme.MaterialThemeFromMdcTheme
import timber.log.Timber

val BackStack = ambientOf<BackStack<AppRoute>> { error("No backstack available") }

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    //Database
    private lateinit var database: AppDatabase

    //ViewModels
    private val model: MainViewModel by viewModels()
    private val chartsModel: ChartsViewModel by viewModels()
    private val detailsViewModel: DetailViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()
    private val historyViewModel: HistoryViewModel by viewModels()

    private val onOpenInBrowser: (ListingMin) -> Unit = {
        onOpenInBrowserClicked(it, this)
    }

    private lateinit var onListingClicked: (ListingMin) -> Unit

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        database =  provideDatabase(this)

        setContent {
            MaterialThemeFromMdcTheme  {
                Router<AppRoute>(start = AppRoute.LocalRoute) { currentRoute ->
                    Providers(BackStack provides this@Router) {
                        onListingClicked = {
                            this.push(AppRoute.DetailRoute(item = it, onOpenInBrowser = onOpenInBrowser))
                        }

                        var showDialog by state { false }

                        if (showDialog) {
                            PeriodeSelectDialog(onSelect = {
                                userViewModel.updatePeriod(it)
                                showDialog = false
                            }, onDismiss = {
                                showDialog = false
                            })
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
                                                }
                                            }) {
                                                Icon(vectorResource(id = menuAction.icon))
                                            }
                                        }
                                    }
                                )
                            },
                            bodyContent = {
                                AppContent()
                            },
                            bottomAppBar = {
                                BottomNavigationBar(
                                    items = listOf(
                                        AppRoute.ChartRoute,
                                        AppRoute.LocalRoute,
                                        AppRoute.HistoryRoute,
                                        AppRoute.ProfileRoute(onClick = {
                                            showDialog = true
                                        })
                                    ),
                                    backStack = this@Router)
                            }
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun AppContent() {
        val sessionStatus by model.sessionStatus.observeAsState(SessionStatus.LoggedOut)

        Crossfade(BackStack.current.current.data) { screen ->
            when(screen) {
                is AppRoute.ChartRoute -> ChartScreen(model = chartsModel, onListingSelected = onListingClicked)
                is AppRoute.HistoryRoute ->  {
                    when(sessionStatus) {
                        is SessionStatus.LoggedOut -> LoginScreen(context = this)
                        is SessionStatus.LoggedIn -> HistoryScreen(historyViewModel, onListingSelected = onListingClicked)
                    }
                }
                is AppRoute.LocalRoute -> LocalScreen()
                is AppRoute.ProfileRoute -> {
                    when(sessionStatus) {
                        is SessionStatus.LoggedOut -> LoginScreen(context = this)
                        is SessionStatus.LoggedIn -> {
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

    @Composable
    private fun PeriodeSelectDialog(onSelect: (selected: TimePeriod) -> Unit, onDismiss: () -> Unit) {
        var selected by state { userViewModel.timePeriod.value }
        val radioGroupOptions = TimePeriod.values().asList()
        AlertDialog(
            onCloseRequest = { onDismiss() },
            title = { Text(text = "Zeitrahmen") },
            text = {
                RadioGroup {
                    radioGroupOptions.forEach {
                        RadioGroupTextItem(selected = selected == it, onSelect = {
                            selected = it
                        }, text = it.niceName)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { onSelect(selected) }, contentColor = MaterialTheme.colors.secondary) {
                    Text(text = "Select")
                }
            }
        )
    }
}

const val AUTH_ENDPOINT = "https://www.last.fm/api/auth/"
const val REDIRECT_URL = "de.schnettler.scrobble://auth"
const val API_KEY = "***REPLACE_WITH_LASTFM_API_KEY***"