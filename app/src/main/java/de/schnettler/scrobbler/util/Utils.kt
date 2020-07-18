package de.schnettler.scrobbler.util

import android.content.Context
import android.icu.text.DateFormat
import android.net.Uri
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.State
import androidx.core.text.HtmlCompat
import androidx.ui.input.TextFieldValue
import androidx.ui.unit.dp
import de.schnettler.database.models.LocalTrack
import java.util.*
import java.util.concurrent.TimeUnit


val defaultSpacerSize = 16.dp
val cardCornerRadius = 12.dp

fun Context.toast(message: CharSequence, duration: Int = Toast.LENGTH_SHORT) =
    Toast.makeText(this, message, duration).show()

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


inline fun <T> Iterable<T>.sumByLong(selector: (T) -> Long): Long {
    var sum: Long = 0
    for (element in this) {
        sum += selector(element)
    }
    return sum
}

fun String.fromHtml() = HtmlCompat.fromHtml(this, HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
fun String.fromHtmlLastFm() = this.fromHtml().removeSuffix("Read more on Last.fm.")

fun LocalTrack.copyByState(
    trackState: State<TextFieldValue>,
    artistState: State<TextFieldValue>,
    albumState: State<TextFieldValue>
) = this.copy(
    name = trackState.value.text,
    artist = artistState.value.text,
    album = albumState.value.text
)

fun milliSecondsToMinSeconds(input: Long) = String.format("%d:%d",
        TimeUnit.MILLISECONDS.toMinutes(input),
        TimeUnit.MILLISECONDS.toSeconds(input) -
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(input)))


const val PACKAGE_YT_MUSIC = "com.google.android.apps.youtube.music"

fun packageNameToAppName(packageName: String) = when (packageName) {
    PACKAGE_YT_MUSIC -> "YouTube Music"
    else -> packageName
}

fun Long.milliSecondsToDate() =
        DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT,Locale.getDefault()).format(Date(this))