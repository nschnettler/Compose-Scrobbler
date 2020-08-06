package de.schnettler.scrobbler

import androidx.ui.graphics.vector.VectorAsset
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.outlined.AccountCircle
import androidx.ui.material.icons.rounded.BarChart
import androidx.ui.material.icons.rounded.History
import androidx.ui.material.icons.rounded.Search
import de.schnettler.database.models.LastFmEntity
import de.schnettler.scrobbler.util.MenuAction

sealed class AppRoute(
    val title: String,
    val icon: VectorAsset,
    val menuActions: List<MenuAction> = listOf()
) {
    object ChartRoute : AppRoute("Charts", Icons.Rounded.BarChart)
    object LocalRoute : AppRoute("History", Icons.Rounded.History)
    class ProfileRoute(onFilterClicked: () -> Unit) : AppRoute(
        title = "Profile",
        icon = Icons.Outlined.AccountCircle,
        menuActions = listOf(MenuAction.Period(onFilterClicked))
    )

    class DetailRoute(val item: LastFmEntity, onOpenInBrowser: (LastFmEntity) -> Unit) : AppRoute(
        title = item.name,
        icon = Icons.Outlined.AccountCircle,
        menuActions = listOf(MenuAction.OpenInBrowser(onClick = onOpenInBrowser))
    )

    object SearchRoute : AppRoute("Search", Icons.Rounded.Search)
}