 package de.schnettler.scrobbler.compose.widget

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.BottomNavigationDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

 /**
  * A wrapper around [NavigationBar] which supports the setting of [contentPadding] to add
  * internal padding. This is especially useful in conjunction with insets.
  */
 @Composable
 fun NavigationBar(
     modifier: Modifier = Modifier,
     contentPadding: PaddingValues = PaddingValues(0.dp),
     backgroundColor: Color = MaterialTheme.colorScheme.surface,
     contentColor: Color = contentColorFor(backgroundColor),
     elevation: Dp = BottomNavigationDefaults.Elevation,
     content: @Composable RowScope.() -> Unit,
 ) {
     BottomNavigationSurface(modifier, backgroundColor, contentColor, elevation) {
         BottomNavigationContent(Modifier.padding(contentPadding)) {
             content()
         }
     }
 }

 @Composable
 fun BottomNavigationSurface(
     modifier: Modifier = Modifier,
     backgroundColor: Color = MaterialTheme.colorScheme.surface,
     contentColor: Color = contentColorFor(backgroundColor),
     tonalElevation: Dp = BottomNavigationDefaults.Elevation,
     content: @Composable () -> Unit
 ) {
     Surface(
         color = backgroundColor,
         contentColor = contentColor,
         tonalElevation = tonalElevation,
         modifier = modifier,
     ) {
         content()
     }
 }

 @Composable
 fun BottomNavigationContent(
     modifier: Modifier = Modifier,
     content: @Composable RowScope.() -> Unit,
 ) {
     Row(
         modifier = modifier
             .fillMaxWidth()
             .height(BottomNavigationHeight)
             .selectableGroup(),
         horizontalArrangement = Arrangement.SpaceBetween,
         content = content,
     )
 }

 /**
  * Copied from [androidx.compose.material3.NavigationBar]
  * Height of a [NavigationBar] component
  */
 private val BottomNavigationHeight = 80.dp