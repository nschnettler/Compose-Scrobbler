package de.schnettler.scrobbler.model.remote

interface StatsResponse {
    val listeners: Long
    val playcount: Long
    val userplaycount: Long
}