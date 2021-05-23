package de.schnettler.scrobbler.charts.preview

import de.schnettler.scrobbler.model.EntityType
import de.schnettler.scrobbler.model.LastFmEntity
import de.schnettler.scrobbler.model.ListType
import de.schnettler.scrobbler.model.TopListArtist
import de.schnettler.scrobbler.model.TopListEntry

internal object PreviewUtils {
    fun generateFakeArtistCharts(number: Int) = MutableList(number) { index ->
        TopListArtist(
            TopListEntry(
                "",
                EntityType.ARTIST,
                ListType.CHART,
                index,
                10
            ),
            LastFmEntity.Artist("Artist $index")
        )
    }
}