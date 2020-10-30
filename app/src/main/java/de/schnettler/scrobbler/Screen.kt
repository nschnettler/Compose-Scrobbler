package de.schnettler.scrobbler

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.Icons.Rounded
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Search
import androidx.compose.ui.graphics.vector.VectorAsset
import de.schnettler.database.models.LastFmEntity
import de.schnettler.scrobbler.util.MenuAction

sealed class Screen(
    val routeId: String,
    @StringRes val titleId: Int,
    val icon: VectorAsset,
    val menuActions: List<MenuAction> = emptyList(),
) {
    companion object {
        const val detailsRoute = "details"
    }
    object Charts : Screen(routeId = "chart", titleId = R.string.nav_charts, icon = Rounded.BarChart)
    object History : Screen(routeId = "history", titleId = R.string.nav_history, icon = Rounded.History)
    object Search : Screen(routeId = "search", titleId = R.string.nav_search, icon = Rounded.Search)
    object Profile : Screen(
        routeId = "profile",
        titleId = R.string.nav_profile,
        icon = Icons.Outlined.AccountCircle,
        menuActions = listOf(MenuAction.Period)
    )

    object Settings : Screen(routeId = "settings", R.string.nav_settings, Icons.Outlined.Settings)
    class Details(val item: LastFmEntity) : Screen(
        routeId = detailsRoute,
        titleId = R.string.nav_history,
        icon = Icons.Outlined.AccountCircle,
        menuActions = listOf(MenuAction.OpenInBrowser(item.url))
    )
}