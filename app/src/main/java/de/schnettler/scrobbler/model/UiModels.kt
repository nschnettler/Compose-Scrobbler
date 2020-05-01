package de.schnettler.scrobbler.model

import androidx.ui.graphics.vector.VectorAsset

data class ListItem(
    val title: String,
    val subtitle: String,
    val imageUrl: String
)

data class MenuItem(
    val title: String,
    val icon: VectorAsset
)