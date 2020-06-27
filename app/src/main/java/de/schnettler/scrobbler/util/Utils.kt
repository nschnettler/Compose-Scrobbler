package de.schnettler.scrobbler.util

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.Composable
import androidx.ui.material.Surface
import androidx.ui.unit.dp
import de.schnettler.database.models.ListingMin
import dev.chrisbanes.accompanist.mdctheme.MaterialThemeFromMdcTheme
import java.util.*


val defaultSpacerSize = 16.dp
val cardCornerRadius = 12.dp

fun Context.toast(message: CharSequence) =
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

//https://stackoverflow.com/a/50963795/12743428
fun String.toFlagEmoji(): String {
    // 1. It first checks if the string consists of only 2 characters: ISO 3166-1 alpha-2 two-letter country codes (https://en.wikipedia.org/wiki/Regional_Indicator_Symbol).
    if (this.length != 2) {
        return ""
    }

    val countryCodeCaps = this.toUpperCase() // upper case is important because we are calculating offset
    val firstLetter = Character.codePointAt(countryCodeCaps, 0) - 0x41 + 0x1F1E6
    val secondLetter = Character.codePointAt(countryCodeCaps, 1) - 0x41 + 0x1F1E6

    // 2. It then checks if both characters are alphabet
    if (!countryCodeCaps[0].isLetter() || !countryCodeCaps[1].isLetter()) {
        return this
    }

    return String(Character.toChars(firstLetter)) + String(Character.toChars(secondLetter))
}

//https://stackoverflow.com/a/38588988/12743428
fun String.toCountryCode() = Locale.getISOCountries().find {
    Locale("", it).getDisplayCountry(Locale.ENGLISH) == this
}

fun String.firstLetter() = this.first { it.isLetter() }.toString()


fun Context.openUrlInCustomTab(url: String) {
    val builder = CustomTabsIntent.Builder()
    val customTabsIntent = builder.build()
    customTabsIntent.launchUrl(this, Uri.parse(url))
}

fun onOpenInBrowserClicked(listing: ListingMin, activity: Context) {
    listing.url?.let {url ->
        activity.openUrlInCustomTab(url)
    }
}

@Composable
internal fun ThemedPreview(
    darkTheme: Boolean = false,
    children: @Composable() () -> Unit
) {
    MaterialThemeFromMdcTheme {
        Surface {
            children()
        }
    }
}