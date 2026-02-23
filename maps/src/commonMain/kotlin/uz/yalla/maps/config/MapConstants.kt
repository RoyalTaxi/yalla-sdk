package uz.yalla.maps.config

import androidx.compose.ui.unit.dp
import io.github.dellisd.spatialk.geojson.BoundingBox

object MapConstants {
    // Zoom levels
    const val DEFAULT_ZOOM = 15.0
    const val ZOOM_MIN = 4.0
    const val ZOOM_MAX = 21.0
    const val SEARCH_MIN_ZOOM = 13f
    const val EXECUTORS_VISIBLE_MIN_ZOOM = 8.0

    // Uzbekistan bounds
    val UZBEKISTAN_BOUNDING_BOX =
        BoundingBox(
            west = 55.996639,
            south = 37.172764,
            east = 73.132278,
            north = 45.590075
        )

    val UZBEKISTAN_CENTER = (37.172764 + 45.590075) / 2 to (55.996639 + 73.132278) / 2
    val ANDIJAN_CENTER = 40.7821 to 72.3442

    // Zahiriddin Muhammad Bobur square (Andijan)
    val BOBUR_SQUARE = 40.761746 to 72.351894

    // Padding
    val DEFAULT_PADDING = 60.dp

    // Map styles
    const val LIGHT_STYLE_URL = "https://basemaps.cartocdn.com/gl/positron-gl-style/style.json"
    const val DARK_STYLE_URL = "https://basemaps.cartocdn.com/gl/dark-matter-gl-style/style.json"
}
