package de.schnettler.scrobbler.util

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent

fun Context.toast(message: CharSequence, duration: Int = Toast.LENGTH_SHORT) =
    Toast.makeText(this, message, duration).show()

fun Context.openUrlInCustomTab(url: String) {
    val builder = CustomTabsIntent.Builder()
    val customTabsIntent = builder.build()
    customTabsIntent.launchUrl(this, Uri.parse(url))
}