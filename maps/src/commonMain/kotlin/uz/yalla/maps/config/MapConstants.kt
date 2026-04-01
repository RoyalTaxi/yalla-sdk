package uz.yalla.maps.config

import androidx.compose.ui.unit.dp
import io.github.dellisd.spatialk.geojson.BoundingBox

/**
 * Global map configuration constants shared across all map provider implementations.
 *
 * Contains zoom-level bounds, geographic reference points for Uzbekistan, default UI padding,
 * and MapLibre tile-style URLs. Values are consumed by [MapController][uz.yalla.maps.api.MapController]
 * implementations, composable map layers, and search-related UI logic.
 *
 * @since 0.0.1
 */
object MapConstants {
    /**
     * Default zoom level used when moving to a point without an explicit zoom.
     *
     * Corresponds roughly to a city-block view at typical screen densities.
     *
     * @since 0.0.1
     */
    const val DEFAULT_ZOOM = 15.0

    /**
     * Minimum zoom level enforced by [GoogleMapController][uz.yalla.maps.provider.google.GoogleMapController]
     * and [LibreMapController][uz.yalla.maps.provider.libre.LibreMapController].
     *
     * At this level the entire country of Uzbekistan is visible.
     *
     * @since 0.0.1
     */
    const val ZOOM_MIN = 4.0

    /**
     * Maximum zoom level enforced by controllers.
     *
     * At this level individual buildings and street details are clearly visible.
     *
     * @since 0.0.1
     */
    const val ZOOM_MAX = 21.0

    /**
     * Minimum zoom level required before address-search geocoding results are considered
     * meaningful for the current viewport.
     *
     * Below this zoom, the viewport is too wide for location-pin placement to be useful.
     *
     * @since 0.0.1
     */
    const val SEARCH_MIN_ZOOM = 13f

    /**
     * Minimum zoom level at which executor (driver) markers become visible on the map.
     *
     * Below this zoom, executor markers are hidden to avoid visual clutter.
     *
     * @since 0.0.1
     */
    const val EXECUTORS_VISIBLE_MIN_ZOOM = 8.0

    /**
     * Geographic bounding box encompassing Uzbekistan.
     *
     * Used by MapLibre to constrain the initial viewport and by search to limit geocoding results.
     *
     * @since 0.0.1
     */
    val UZBEKISTAN_BOUNDING_BOX =
        BoundingBox(
            west = 55.996639,
            south = 37.172764,
            east = 73.132278,
            north = 45.590075
        )

    /**
     * Approximate geographic center of Uzbekistan as a (latitude, longitude) pair.
     *
     * Computed as the midpoint of [UZBEKISTAN_BOUNDING_BOX].
     *
     * @since 0.0.1
     */
    val UZBEKISTAN_CENTER = (37.172764 + 45.590075) / 2 to (55.996639 + 73.132278) / 2

    /**
     * Geographic center of Andijan city as a (latitude, longitude) pair.
     *
     * @since 0.0.1
     */
    val ANDIJAN_CENTER = 40.7821 to 72.3442

    /**
     * Zahiriddin Muhammad Bobur square in Andijan; used as the default fallback location
     * when the user's position is unknown.
     *
     * @since 0.0.1
     */
    val BOBUR_SQUARE = 40.761746 to 72.351894

    /**
     * Default padding applied around map bounds when fitting routes via
     * [MapController.fitBounds][uz.yalla.maps.api.MapController.fitBounds].
     *
     * @since 0.0.1
     */
    val DEFAULT_PADDING = 60.dp

    /**
     * CARTO Positron light-mode vector tile style URL for MapLibre.
     *
     * @since 0.0.1
     */
    const val LIGHT_STYLE_URL = "https://basemaps.cartocdn.com/gl/positron-gl-style/style.json"

    /**
     * CARTO Dark Matter dark-mode vector tile style URL for MapLibre.
     *
     * @since 0.0.1
     */
    const val DARK_STYLE_URL = "https://basemaps.cartocdn.com/gl/dark-matter-gl-style/style.json"
}
