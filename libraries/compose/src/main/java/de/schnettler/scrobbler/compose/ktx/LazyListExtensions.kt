package de.schnettler.scrobbler.compose.ktx

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp

fun LazyListScope.itemSpacer(height: Dp) {
    item {
        Spacer(Modifier.height(height).fillParentMaxWidth())
    }
}