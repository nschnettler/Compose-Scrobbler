package de.schnettler.scrobbler

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.Providers
import androidx.preference.PreferenceManager
import androidx.ui.core.setContent
import androidx.ui.material.Scaffold
import com.github.zsoltk.compose.backpress.AmbientBackPressHandler
import com.github.zsoltk.compose.backpress.BackPressHandler
import com.github.zsoltk.compose.router.Router
import com.tfcporciuncula.flow.FlowSharedPreferences
import dagger.hilt.android.AndroidEntryPoint
import de.schnettler.composepreferences.AmbientPreferences
import de.schnettler.database.models.LastFmEntity
import de.schnettler.scrobbler.components.BottomNavigationBar
import de.schnettler.scrobbler.screens.AppContent
import de.schnettler.scrobbler.screens.ToolBar
import de.schnettler.scrobbler.theme.AppTheme
import de.schnettler.scrobbler.util.REDIRECT_URL
import de.schnettler.scrobbler.util.openUrlInCustomTab
import de.schnettler.scrobbler.viewmodels.ChartsViewModel
import de.schnettler.scrobbler.viewmodels.DetailViewModel
import de.schnettler.scrobbler.viewmodels.LocalViewModel
import de.schnettler.scrobbler.viewmodels.MainViewModel
import de.schnettler.scrobbler.viewmodels.SearchViewModel
import de.schnettler.scrobbler.viewmodels.UserViewModel
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

    private val onOpenInBrowser: (LastFmEntity) -> Unit = {
        openUrlInCustomTab(it.url)
    }

    private val onTagClicked: (String) -> Unit = { tag ->
        val url = "https://www.last.fm/tag/$tag"
        openUrlInCustomTab(url)
    }

    private lateinit var onListingClicked: (LastFmEntity) -> Unit

    private val bottomNavDestinations = listOf(
        AppRoute.ChartRoute,
        AppRoute.LocalRoute,
        AppRoute.SearchRoute,
        AppRoute.ProfileRoute(onFilterClicked = {
            userViewModel.showDialog(true)
        }),
        AppRoute.SettingsRoute
    )

    private val startScreen: AppRoute = AppRoute.LocalRoute
    private val backPressHandler = BackPressHandler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Providers(
                AmbientBackPressHandler provides backPressHandler,
                AmbientPreferences provides FlowSharedPreferences(PreferenceManager.getDefaultSharedPreferences(this))
            ) {
                AppTheme {
                    Router(defaultRouting = startScreen) { backstack ->
                        onListingClicked = {
                            backstack.push(
                                AppRoute.DetailRoute(item = it, onOpenInBrowser = onOpenInBrowser)
                            )
                        }

                        Scaffold(
                            topBar = { ToolBar(currentScreen = backstack.last()) },
                            bodyContent = {
                                AppContent(
                                    backstack.last(),
                                    model,
                                    chartsModel,
                                    detailsViewModel,
                                    userViewModel,
                                    localViewModel,
                                    searchViewModel,
                                    onListingClicked,
                                    onTagClicked
                                )
                            },
                            bottomBar = {
                                BottomNavigationBar(
                                    items = bottomNavDestinations,
                                    currentScreen = backstack.last()
                                ) { newScreen -> backstack.replace(newScreen) }
                            }
                        )
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        if (!backPressHandler.handle()) super.onBackPressed()
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