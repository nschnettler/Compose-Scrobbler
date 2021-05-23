package de.schnettler.scrobbler.submission.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NowPlayingResponse(
    val artist: CorrectionResponse,
    val album: CorrectionResponse,
    val albumArtist: CorrectionResponse,
    val track: CorrectionResponse,
    val ignoredMessage: IgnoredResponse
)