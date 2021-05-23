package de.schnettler.scrobbler.ui.common.compose.model

import androidx.annotation.StringRes
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.schnettler.scrobbler.ui.common.util.R

enum class MediaCardSize(@StringRes val nameRes: Int, val size: Dp) {
    MINI(R.string.media_card_mini, 144.dp),
    SMALL(R.string.media_card_small, 160.dp),
    MEDIUM(R.string.media_card_medium, 176.dp),
    BIG(R.string.media_card_large, 200.dp)
}