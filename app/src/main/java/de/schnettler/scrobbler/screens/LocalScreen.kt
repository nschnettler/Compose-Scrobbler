package de.schnettler.scrobbler.screens

import android.content.Intent
import androidx.compose.Composable
import androidx.compose.collectAsState
import androidx.compose.getValue
import androidx.core.content.ContextCompat.startActivity
import androidx.ui.core.ContextAmbient
import androidx.ui.foundation.Text
import androidx.ui.material.Button
import de.schnettler.database.models.Track
import de.schnettler.scrobbler.components.GenericAdapterList
import de.schnettler.scrobbler.service.MediaListenerService
import de.schnettler.scrobbler.viewmodels.LocalViewModel
import timber.log.Timber

@Composable
fun LocalScreen(localViewModel: LocalViewModel) {
   val context = ContextAmbient.current
   when(MediaListenerService.isEnabled(context)) {
      true -> Content(localViewModel = localViewModel)
      false -> Button(onClick = {
         context.startActivity( Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"))
      }, text = {Text(text = "Enable")})
   }
   Timber.i("Started Service")
}

@Composable
fun Content(localViewModel: LocalViewModel) {
   val data by localViewModel.data.collectAsState()

   data?.let {list ->
      GenericAdapterList(data = list.map {
         Track(name = it.title, artist = it.artist, album = it.album, duration = it.duration, url = "")
      }, onListingSelected = {})
   }
}