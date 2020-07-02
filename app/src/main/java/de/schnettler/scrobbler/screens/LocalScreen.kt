package de.schnettler.scrobbler.screens

import android.content.Intent
import androidx.compose.Composable
import androidx.core.content.ContextCompat.startActivity
import androidx.ui.core.ContextAmbient
import androidx.ui.foundation.Text
import androidx.ui.material.Button
import de.schnettler.scrobbler.service.MediaListenerService
import timber.log.Timber

@Composable
fun LocalScreen() {
   val context = ContextAmbient.current
   when(MediaListenerService.isEnabled(context)) {
      true -> Text(text = "Service enabled")
      false -> Button(onClick = {
         context.startActivity( Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"))
      }, text = {Text(text = "Enable")})
   }
   Timber.i("Started Service")
}