package de.schnettler.scrobbler

import androidx.ui.graphics.vector.VectorAsset
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.outlined.AccountCircle
import androidx.ui.material.icons.outlined.Favorite
import androidx.ui.material.icons.outlined.FavoriteBorder
import androidx.ui.material.icons.outlined.Home

sealed class Screen(val title: String, val icon: VectorAsset) {
    object Charts : Screen("Charts", Icons.Outlined.FavoriteBorder)
    object History : Screen("History", Icons.Outlined.Home)
    object Local: Screen("Local", Icons.Outlined.Favorite)
    object Profile : Screen("Profile", Icons.Outlined.AccountCircle)
}