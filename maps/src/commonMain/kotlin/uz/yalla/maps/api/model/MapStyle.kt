package uz.yalla.maps.api.model

import androidx.compose.runtime.Immutable

/**
 * Light and dark style URLs for a map backend.
 *
 * MapLibre backends use vector-tile style JSON URLs; Google Maps uses
 * empty strings since styling is handled natively.
 */
@Immutable
data class MapStyle(
    val lightStyleUrl: String,
    val darkStyleUrl: String
) {
    companion object {
        /**
         * CARTO Positron (light) / Dark Matter (dark) tile styles for MapLibre.
         */
        val CARTO =
            MapStyle(
                lightStyleUrl = "https://basemaps.cartocdn.com/gl/positron-gl-style/style.json",
                darkStyleUrl = "https://basemaps.cartocdn.com/gl/dark-matter-gl-style/style.json"
            )

        /**
         * Empty style for Google Maps (styling handled natively).
         */
        val GOOGLE =
            MapStyle(
                lightStyleUrl = "",
                darkStyleUrl = ""
            )
    }
}
