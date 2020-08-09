package de.schnettler.repo.util

import android.content.Context
import android.content.SharedPreferences

private fun Context.defaultSharedPrefName() = packageName + "_preferences"

fun Context.defaultSharedPrefs(): SharedPreferences = getSharedPreferences(
    defaultSharedPrefName(),
    Context.MODE_PRIVATE
)