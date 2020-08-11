package de.schnettler.scrobbler.components

import androidx.compose.foundation.Box
import androidx.compose.foundation.ContentGravity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun LoadingScreen(modifier: Modifier = Modifier.fillMaxSize()) {
    Box(modifier = modifier, gravity = ContentGravity.Center) {
        CircularProgressIndicator(
            color = MaterialTheme.colors.secondary,
            modifier = Modifier.wrapContentWidth(Alignment.CenterHorizontally)
        )
    }
}