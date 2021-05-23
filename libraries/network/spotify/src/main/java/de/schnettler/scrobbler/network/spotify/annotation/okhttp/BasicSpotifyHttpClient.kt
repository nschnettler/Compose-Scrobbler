package de.schnettler.scrobbler.network.spotify.annotation.okhttp

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FUNCTION)
annotation class BasicSpotifyHttpClient