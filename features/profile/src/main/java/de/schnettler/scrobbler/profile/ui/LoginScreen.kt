package de.schnettler.scrobbler.profile.ui

import android.content.Context
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import de.schnettler.scrobbler.core.BuildConfig
import de.schnettler.scrobbler.core.util.AUTH_ENDPOINT
import de.schnettler.scrobbler.core.util.REDIRECT_URL
import de.schnettler.scrobbler.profile.R
import timber.log.Timber

@Composable
fun LoginScreen(context: Context = LocalContext.current) {
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