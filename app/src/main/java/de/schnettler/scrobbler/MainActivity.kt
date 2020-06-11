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
import com.github.zsoltk.compose.backpress.AmbientBackPressHandler
import com.github.zsoltk.compose.backpress.BackPressHandler
import com.github.zsoltk.compose.router.BackStack
import com.github.zsoltk.compose.router.Router
import dagger.hilt.android.AndroidEntryPoint
import de.schnettler.common.TimePeriod
import de.schnettler.database.AppDatabase
import de.schnettler.database.provideDatabase
import de.schnettler.scrobbler.components.BottomNavigationBar
import de.schnettler.scrobbler.screens.*
import de.schnettler.scrobbler.screens.details.DetailScreen
import de.schnettler.scrobbler.util.SessionStatus
import de.schnettler.scrobbler.viewmodels.*
import dev.chrisbanes.accompanist.mdctheme.MaterialThemeFromMdcTheme
import timber.log.Timber

val BackStack = ambientOf<BackStack<Screen>> { error("No backstack available") }

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

    private val backPressHandler = BackPressHandler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        database =  provideDatabase(this)

        setContent {
            Providers(
                AmbientBackPressHandler provides backPressHandler
            ) {
                MaterialThemeFromMdcTheme  {
                    Router(defaultRouting = Screen.Local as Screen) {backStack ->
                        Providers(BackStack provides backStack) {

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
                                        title = { Text(text = backStack.last().title) },
                                        actions = {
                                            backStack.last().menuActions.forEach {
                                                IconButton(onClick = it.onClick) {
                                                    Icon(vectorResource(id = it.icon))
                                                }
                                            }
                                        }
                                    )
                                },
                                bodyContent = {
                                    AppContent()
                                },
                                bottomAppBar = {
                                    BottomNavigationBar(items = listOf(
                                        Screen.Charts,
                                        Screen.Local,
                                        Screen.History,
                                        Screen.Profile(onClick = {
                                            showDialog = true
                                        })
                                    ))
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun AppContent() {
        val sessionStatus by model.sessionStatus.observeAsState(SessionStatus.LoggedOut)
        
        Crossfade(BackStack.current.last()) { screen ->
            when(screen) {
                is Screen.Charts -> ChartScreen(model = chartsModel)
                is Screen.History ->  {
                    when(sessionStatus) {
                        is SessionStatus.LoggedOut -> LoginScreen(context = this)
                        is SessionStatus.LoggedIn -> HistoryScreen(historyViewModel)
                    }
                }
                is Screen.Local -> LocalScreen()
                is Screen.Profile -> {
                    when(sessionStatus) {
                        is SessionStatus.LoggedOut -> LoginScreen(context = this)
                        is SessionStatus.LoggedIn -> {
                            val backstack = BackStack.current
                            ProfileScreen(userViewModel, onEntrySelected = {
                                backstack.push(Screen.Detail(it))
                            })
                        }
                    }
                }
                is Screen.Detail -> {
                    detailsViewModel.updateEntry(screen.item)
                    DetailScreen(model = detailsViewModel)
                }
            }
        }
    }

    override fun onBackPressed() {
        if (!backPressHandler.handle()) {
            super.onBackPressed()
        }
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