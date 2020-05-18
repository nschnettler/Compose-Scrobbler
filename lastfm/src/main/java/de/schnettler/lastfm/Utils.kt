package de.schnettler.lastfm

import java.util.*

fun String.encodeBase64(): String = Base64.getEncoder().encodeToString(this.toByteArray())