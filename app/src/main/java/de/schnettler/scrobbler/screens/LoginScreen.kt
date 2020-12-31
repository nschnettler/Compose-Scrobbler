package de.schnettler.scrobbler.screens

import android.content.Context
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.AmbientContext
import androidx.compose.ui.res.stringResource
import de.schnettler.common.BuildConfig
import de.schnettler.scrobbler.R
import de.schnettler.scrobbler.util.AUTH_ENDPOINT
import de.schnettler.scrobbler.util.REDIRECT_URL
import timber.log.Timber

@Composable
fun LoginScreen(context: Context = AmbientContext.current) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Button(onClick = {
           val builder = CustomTabsIntent.Builder()
           val customTabsIntent = builder.build()
           customTabsIntent.launchUrl(
              context,
              Uri.parse("$AUTH_ENDPOINT?api_key=${BuildConfig.LASTFM_API_KEY}&cb=$REDIRECT_URL")
                 .also { Timber.i(it.toString()) }
           )
        }) {
            Text(text = stringResource(id = R.string.login_button))
        }
    }
}