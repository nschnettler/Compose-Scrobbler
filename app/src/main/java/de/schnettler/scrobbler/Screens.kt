package de.schnettler.scrobbler

import androidx.annotation.DrawableRes
import de.schnettler.database.models.LastFmEntity
import de.schnettler.database.models.LastFmStatsEntity
import de.schnettler.scrobbler.util.MenuAction


sealed class AppRoute(
    open val title: String,
    open @DrawableRes val icon: Int,
    open val menuActions: List<MenuAction>
) {
    sealed class MainRoute(
        override val title: String,
        override val icon: Int,
        override val menuActions: List<MenuAction> = listOf()
    ): AppRoute(title, icon, menuActions) {
        object ChartRoute : MainRoute("Charts", R.drawable.ic_round_insights_24)
        object HistoryRoute : MainRoute("History", R.drawable.ic_round_history_24)
        object LocalRoute : MainRoute("Local", R.drawable.ic_round_favorite_border_24)
        class ProfileRoute(onFilterClicked: () -> Unit) : MainRoute(
            title = "Profile",
            icon = R.drawable.ic_outline_account_circle_24,
            menuActions = listOf(MenuAction.Period(onFilterClicked))
        )
    }
    sealed class NestedRoute(
        override val title: String,
        override val icon: Int,
        override val menuActions: List<MenuAction> = listOf()
    ): AppRoute(title, icon, menuActions) {
        class DetailRoute(
            val item: LastFmStatsEntity,
            onOpenInBrowser: (LastFmEntity) -> Unit
        ) : NestedRoute(
            title = item.name,
            icon = R.drawable.ic_outline_account_circle_24,
            menuActions = listOf(MenuAction.OpenInBrowser(onClick = onOpenInBrowser))
        )
    }
}