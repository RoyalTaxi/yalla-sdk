package uz.yalla.maps.config

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.dellisd.spatialk.geojson.BoundingBox

public object MapConstants {
    public const val DEFAULT_ZOOM: Double = 15.0

    public const val ZOOM_MIN: Double = 4.0

    public const val ZOOM_MAX: Double = 21.0

    public const val FIT_ZOOM_MAX: Double = 17.0

    public const val SEARCH_MIN_ZOOM: Float = 13f

    public const val EXECUTORS_VISIBLE_MIN_ZOOM: Double = 8.0

    public val UZBEKISTAN_BOUNDING_BOX: BoundingBox =
        BoundingBox(
            west = 55.996639,
            south = 37.172764,
            east = 73.132278,
            north = 45.590075
        )

    public val UZBEKISTAN_CENTER: Pair<Double, Double> = (37.172764 + 45.590075) / 2 to (55.996639 + 73.132278) / 2

    public val ANDIJAN_CENTER: Pair<Double, Double> = 40.7821 to 72.3442

    public val BOBUR_SQUARE: Pair<Double, Double> = 40.761746 to 72.351894

    public val DEFAULT_PADDING: Dp = 60.dp

    public const val LIGHT_STYLE_URL: String = "https://basemaps.cartocdn.com/gl/positron-gl-style/style.json"

    public const val DARK_STYLE_URL: String = "https://basemaps.cartocdn.com/gl/dark-matter-gl-style/style.json"
}
