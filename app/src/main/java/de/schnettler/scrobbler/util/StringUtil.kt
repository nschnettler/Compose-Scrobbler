package de.schnettler.scrobbler.util

import androidx.core.text.HtmlCompat
import java.util.Locale

const val PACKAGE_YT_MUSIC = "com.google.android.apps.youtube.music"

// https://stackoverflow.com/a/50963795/12743428
fun String.toFlagEmoji(): String {
    // 1. It first checks if the string consists of only 2 characters:
    // ISO 3166-1 alpha-2 two-letter country codes (https://en.wikipedia.org/wiki/Regional_Indicator_Symbol).
    if (this.length != 2) {
        return ""
    }

    val countryCodeCaps =
        this.toUpperCase() // upper case is important because we are calculating offset
    val firstLetter = Character.codePointAt(countryCodeCaps, 0) - 0x41 + 0x1F1E6
    val secondLetter = Character.codePointAt(countryCodeCaps, 1) - 0x41 + 0x1F1E6

    // 2. It then checks if both characters are alphabet
    return if (!countryCodeCaps[0].isLetter() || !countryCodeCaps[1].isLetter()) {
        this
    } else String(Character.toChars(firstLetter)) + String(Character.toChars(secondLetter))
}

// https://stackoverflow.com/a/38588988/12743428
fun String.toCountryCode() = Locale.getISOCountries().find {
    Locale("", it).getDisplayCountry(Locale.ENGLISH) == this
}

fun String.firstLetter() = this.first { it.isLetter() }.toString()

fun String.fromHtml() = HtmlCompat.fromHtml(this, HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
fun String.fromHtmlLastFm() = this.fromHtml().removeSuffix("Read more on Last.fm.")

fun packageNameToAppName(packageName: String) = when (packageName) {
    PACKAGE_YT_MUSIC -> "YouTube Music"
    else -> packageName
}