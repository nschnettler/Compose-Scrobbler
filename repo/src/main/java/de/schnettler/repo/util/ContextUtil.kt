package de.schnettler.repo.util

import android.content.Context

private fun Context.defaultSharedPrefName() = packageName + "_preferences"

fun Context.defaultSharedPrefs() = getSharedPreferences(
    defaultSharedPrefName(),
    Context.MODE_PRIVATE
)