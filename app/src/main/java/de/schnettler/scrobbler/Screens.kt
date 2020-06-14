package de.schnettler.scrobbler

import android.content.Context
import androidx.annotation.DrawableRes
import de.schnettler.database.models.ListingMin
import de.schnettler.scrobbler.util.MenuAction
import de.schnettler.scrobbler.util.onOpenInBrowserClicked


sealed class AppRoute(val title: String, @DrawableRes val icon: Int, val menuActions: List<MenuAction> = listOf()) {
    object ChartRoute : AppRoute("Charts", R.drawable.ic_round_insights_24)
    object HistoryRoute : AppRoute("History", R.drawable.ic_round_history_24)
    object LocalRoute: AppRoute("Local", R.drawable.ic_round_favorite_border_24)
    class ProfileRoute(onClick: () -> Unit) : AppRoute("Profile", R.drawable.ic_outline_account_circle_24, listOf(
        MenuAction.Period(onClick)
    ))
    class DetailRoute(val item: ListingMin, context: Context) : AppRoute(item.name, R.drawable.ic_outline_account_circle_24, listOf(
        MenuAction.OpenInBrowser(onClick = {
            onOpenInBrowserClicked(item, context)
        })
    ))
}