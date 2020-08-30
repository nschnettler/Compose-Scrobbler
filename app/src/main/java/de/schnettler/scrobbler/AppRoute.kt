package de.schnettler.scrobbler

import android.os.Parcelable
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
    val title: String
    val icon: VectorAsset
    val menuActions: List<MenuAction>
}

sealed class MainRoute(
    override val title: String,
    override val icon: VectorAsset,
    override val menuActions: List<MenuAction> = listOf()
) : AppRoute, Parcelable {
    @Parcelize object ChartRoute : MainRoute(title = "Charts", icon = Icons.Rounded.BarChart)
    @Parcelize object LocalRoute : MainRoute("History", Icons.Rounded.History)
    @Parcelize object ProfileRoute : MainRoute(title = "Profile", icon = Icons.Outlined.AccountCircle)
    @Parcelize object SearchRoute : MainRoute("Search", Icons.Rounded.Search)
    @Parcelize object SettingsRoute : MainRoute("Settings", Icons.Outlined.Settings)
}

sealed class NestedRoute(
    override val title: String,
    override val icon: VectorAsset,
    override val menuActions: List<MenuAction> = listOf()
) : AppRoute {
    @Parcelize class DetailRoute(val item: LastFmEntity, /*onOpenInBrowser: (LastFmEntity) -> Unit*/) : NestedRoute(
        title = item.name,
        icon = Icons.Outlined.AccountCircle,
//        menuActions = listOf(MenuAction.OpenInBrowser(onClick = onOpenInBrowser))
    )
}