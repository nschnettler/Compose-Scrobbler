package de.schnettler.scrobbler

import android.os.Parcelable
import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Search
import androidx.compose.ui.graphics.vector.VectorAsset
import de.schnettler.database.models.LastFmEntity
import de.schnettler.scrobbler.util.MenuAction
import kotlinx.android.parcel.Parcelize

interface AppRoute : Parcelable {
    val title: Int
    val icon: VectorAsset
    val menuActions: List<MenuAction>
}

sealed class MainRoute(
    @StringRes override val title: Int,
    override val icon: VectorAsset,
    override val menuActions: List<MenuAction> = listOf()
) : AppRoute {
    @Parcelize object ChartRoute : MainRoute(title = R.string.nav_charts, icon = Icons.Rounded.BarChart)
    @Parcelize object LocalRoute : MainRoute(title = R.string.nav_history, Icons.Rounded.History)
    @Parcelize object ProfileRoute : MainRoute(
        title = R.string.nav_profile,
        icon = Icons.Outlined.AccountCircle,
        menuActions = listOf(MenuAction.Period),
    )
    @Parcelize object SearchRoute : MainRoute(R.string.nav_search, Icons.Rounded.Search)
    @Parcelize object SettingsRoute : MainRoute(R.string.nav_settings, Icons.Outlined.Settings)
}

sealed class NestedRoute(
    @StringRes override val title: Int,
    override val icon: VectorAsset,
    override val menuActions: List<MenuAction> = listOf()
) : AppRoute {
    @Parcelize class DetailRoute(val item: LastFmEntity) : NestedRoute(
        title = R.string.nav_history,
        icon = Icons.Outlined.AccountCircle,
        menuActions = listOf(MenuAction.OpenInBrowser(item.url))
    )
}