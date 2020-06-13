package de.schnettler.scrobbler

import android.content.Context
import androidx.annotation.DrawableRes
import de.schnettler.database.models.ListingMin
import de.schnettler.scrobbler.util.MenuAction
import de.schnettler.scrobbler.util.onOpenInBrowserClicked


sealed class Screen(val title: String, @DrawableRes val icon: Int, val menuActions: List<MenuAction> = listOf()) {
    object Charts : Screen("Charts", R.drawable.ic_round_insights_24)
    object History : Screen("History", R.drawable.ic_round_history_24)
    object Local: Screen("Local", R.drawable.ic_round_favorite_border_24)
    class Profile(onClick: () -> Unit) : Screen("Profile", R.drawable.ic_outline_account_circle_24, listOf(
        MenuAction.Period(onClick)
    ))
    class Detail(val item: ListingMin, context: Context) : Screen(item.name, R.drawable.ic_outline_account_circle_24, listOf(
        MenuAction.OpenInBrowser(onClick = {
            onOpenInBrowserClicked(item, context)
        })
    ))
}