package de.schnettler.scrobbler.screens

import android.content.Context
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.Composable
import androidx.ui.core.Modifier
import androidx.ui.foundation.Box
import androidx.ui.foundation.ContentGravity
import androidx.ui.foundation.Text
import androidx.ui.layout.fillMaxSize
import androidx.ui.material.Button
import de.schnettler.common.BuildConfig
import de.schnettler.scrobbler.AUTH_ENDPOINT
import de.schnettler.scrobbler.REDIRECT_URL
import timber.log.Timber

@Composable
fun LoginScreen(context: Context) {
    Box(modifier = Modifier.fillMaxSize(), gravity = ContentGravity.Center) {
        Button(onClick = {
           val builder = CustomTabsIntent.Builder()
           val customTabsIntent = builder.build()
           customTabsIntent.launchUrl(
              context,
              Uri.parse("$AUTH_ENDPOINT?api_key=${BuildConfig.LASTFM_API_KEY}&cb=$REDIRECT_URL")
                 .also { Timber.i(it.toString()) }
           )
        }) {
            Text(text = "Login")
        }
    }
}