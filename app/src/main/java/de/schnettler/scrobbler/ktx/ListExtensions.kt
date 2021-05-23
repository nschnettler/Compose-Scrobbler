package de.schnettler.scrobbler.ktx

fun <T> List<T>.secondOrNull(): T? {
    return if (size < 2) null else this[1]
}