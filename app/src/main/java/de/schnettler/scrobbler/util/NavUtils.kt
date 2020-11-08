package de.schnettler.scrobbler.util

import androidx.navigation.NavBackStackEntry
import androidx.navigation.compose.KEY_ROUTE

fun NavBackStackEntry.route() = arguments?.getString(KEY_ROUTE)