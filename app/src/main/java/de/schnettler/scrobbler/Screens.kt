package de.schnettler.scrobbler

import androidx.annotation.DrawableRes
import de.schnettler.database.models.ListingMin
import de.schnettler.scrobbler.util.MenuAction


sealed class AppRoute(
    val title: String,
    @DrawableRes val icon: Int,
    val menuActions: List<MenuAction> = listOf()
) {
    object ChartRoute : AppRoute("Charts", R.drawable.ic_round_insights_24)
    class HistoryRoute(onRefreshClicked: () -> Unit) : AppRoute(
            title = "History",
            icon = R.drawable.ic_round_history_24,
            menuActions = listOf(MenuAction.Refresh(onClick = onRefreshClicked)))
    object LocalRoute : AppRoute("Local", R.drawable.ic_round_favorite_border_24)
    class ProfileRoute(onFilterClicked: () -> Unit) : AppRoute(
        title = "Profile",
        icon = R.drawable.ic_outline_account_circle_24,
        menuActions = listOf(MenuAction.Period(onFilterClicked))
    )
    class DetailRoute(val item: ListingMin, onOpenInBrowser: (ListingMin) -> Unit) : AppRoute(
        title = item.name,
        icon = R.drawable.ic_outline_account_circle_24,
        menuActions = listOf(MenuAction.OpenInBrowser(onClick = onOpenInBrowser))
    )
}