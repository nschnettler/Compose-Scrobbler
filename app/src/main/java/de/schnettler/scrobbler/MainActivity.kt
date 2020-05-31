package de.schnettler.scrobbler

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.Composable
import androidx.compose.Providers
import androidx.compose.ambientOf
import androidx.compose.getValue
import androidx.lifecycle.lifecycleScope
import androidx.ui.animation.Crossfade
import androidx.ui.core.setContent
import androidx.ui.foundation.Icon
import androidx.ui.foundation.Text
import androidx.ui.livedata.observeAsState
import androidx.ui.material.IconButton
import androidx.ui.material.Scaffold
import androidx.ui.material.TopAppBar
import androidx.ui.res.vectorResource
import com.github.zsoltk.compose.backpress.AmbientBackPressHandler
import com.github.zsoltk.compose.backpress.BackPressHandler
import com.github.zsoltk.compose.router.BackStack
import com.github.zsoltk.compose.router.Router
import de.schnettler.database.AppDatabase
import de.schnettler.database.provideDatabase
import de.schnettler.repo.Repository
import de.schnettler.scrobbler.components.BottomNavigationBar
import de.schnettler.scrobbler.screens.*
import de.schnettler.scrobbler.util.MenuAction
import de.schnettler.scrobbler.util.SessionStatus
import de.schnettler.scrobbler.util.getViewModel
import de.schnettler.scrobbler.viewmodels.*
import dev.chrisbanes.accompanist.mdctheme.MaterialThemeFromMdcTheme
import timber.log.Timber

val BackStack = ambientOf<BackStack<Screen>> { error("No backstack available") }
class MainActivity : AppCompatActivity() {

    //Database
    private lateinit var database: AppDatabase
    private lateinit var repo: Repository

    //ViewModels
    private val model by lazy { getViewModel { MainViewModel(repo) } }
    private val chartsModel by lazy { getViewModel { ChartsViewModel(repo) } }
    private val detailsViewModel by lazy { getViewModel { DetailViewModel(repo) } }
    private val userViewModel by lazy { getViewModel { UserViewModel(repo) } }
    private val historyViewModel by lazy { getViewModel { HistoryViewModel(repo) } }

    private val backPressHandler = BackPressHandler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        database =  provideDatabase(this)
        repo  = Repository(database, lifecycleScope)

        setContent {
            Providers(
                AmbientBackPressHandler provides backPressHandler
            ) {
                MaterialThemeFromMdcTheme  {
                    Router(defaultRouting = Screen.Local as Screen) {backStack ->
                        Providers(BackStack provides backStack) {
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
                                            userViewModel.updatePeriod()
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
}

const val AUTH_ENDPOINT = "https://www.last.fm/api/auth/"
const val REDIRECT_URL = "de.schnettler.scrobble://auth"
const val API_KEY = "***REPLACE_WITH_LASTFM_API_KEY***"