package uz.yalla.maps.config

import androidx.compose.ui.unit.dp
import io.github.dellisd.spatialk.geojson.BoundingBox

/**
 * Global map configuration constants: zoom levels, geographic bounds, padding, and style URLs.
 *
 * @since 0.0.1
 */
object MapConstants {
    /** Default zoom level used when moving to a point without an explicit zoom. */
    const val DEFAULT_ZOOM = 15.0

    /** Minimum zoom level enforced by controllers. */
    const val ZOOM_MIN = 4.0

    /** Maximum zoom level enforced by controllers. */
    const val ZOOM_MAX = 21.0

    /** Minimum zoom required before address-search results become meaningful. */
    const val SEARCH_MIN_ZOOM = 13f

    /** Minimum zoom level at which executor markers become visible. */
    const val EXECUTORS_VISIBLE_MIN_ZOOM = 8.0

    /** Geographic bounding box encompassing Uzbekistan, used to constrain the map viewport. */
    val UZBEKISTAN_BOUNDING_BOX =
        BoundingBox(
            west = 55.996639,
            south = 37.172764,
            east = 73.132278,
            north = 45.590075
        )

    /** Geographic center of Uzbekistan as a (latitude, longitude) pair. */
    val UZBEKISTAN_CENTER = (37.172764 + 45.590075) / 2 to (55.996639 + 73.132278) / 2

    /** Geographic center of Andijan as a (latitude, longitude) pair. */
    val ANDIJAN_CENTER = 40.7821 to 72.3442

    /** Zahiriddin Muhammad Bobur square in Andijan; used as the default fallback location. */
    val BOBUR_SQUARE = 40.761746 to 72.351894

    /** Default padding applied around map bounds when fitting routes. */
    val DEFAULT_PADDING = 60.dp

    /** CARTO Positron light-mode tile style URL for MapLibre. */
    const val LIGHT_STYLE_URL = "https://basemaps.cartocdn.com/gl/positron-gl-style/style.json"

    /** CARTO Dark Matter dark-mode tile style URL for MapLibre. */
    const val DARK_STYLE_URL = "https://basemaps.cartocdn.com/gl/dark-matter-gl-style/style.json"
}
