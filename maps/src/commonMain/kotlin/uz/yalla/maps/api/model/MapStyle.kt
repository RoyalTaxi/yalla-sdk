package uz.yalla.maps.api.model

import androidx.compose.runtime.Immutable

/**
 * Light and dark style URLs for a map backend.
 *
 * MapLibre backends use vector-tile style JSON URLs; Google Maps uses
 * empty strings since styling is handled natively.
 *
 * @property lightStyleUrl URL to the light-mode map style JSON.
 * @property darkStyleUrl URL to the dark-mode map style JSON.
 * @since 0.0.1
 */
@Immutable
data class MapStyle(
    val lightStyleUrl: String,
    val darkStyleUrl: String
) {
    companion object {
        /**
         * CARTO Positron (light) / Dark Matter (dark) tile styles for MapLibre.
         *
         * @since 0.0.1
         */
        val CARTO =
            MapStyle(
                lightStyleUrl = "https://basemaps.cartocdn.com/gl/positron-gl-style/style.json",
                darkStyleUrl = "https://basemaps.cartocdn.com/gl/dark-matter-gl-style/style.json"
            )

        /**
         * Empty style for Google Maps (styling handled natively).
         *
         * @since 0.0.1
         */
        val GOOGLE =
            MapStyle(
                lightStyleUrl = "",
                darkStyleUrl = ""
            )
    }
}
