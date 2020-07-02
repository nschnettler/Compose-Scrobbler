package de.schnettler.scrobbler.service

data class Track(
        val title: String,
        val artist: String,
        val album: String,
        val duration: Long
)