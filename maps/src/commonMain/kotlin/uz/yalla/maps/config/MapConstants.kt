package uz.yalla.maps.config

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Generic, cross-platform map tuning constants shared by the Android/iOS renderers (via SKIE). */
public object MapConstants {
    /** Default camera zoom level. */
    public const val DEFAULT_ZOOM: Double = 15.0

    /** Minimum allowed zoom level. */
    public const val ZOOM_MIN: Double = 4.0

    /** Maximum allowed zoom level. */
    public const val ZOOM_MAX: Double = 21.0

    /** Maximum zoom the camera will reach when framing a set of points. */
    public const val FIT_ZOOM_MAX: Double = 17.0

    /** Minimum zoom at which executor/driver markers become visible. */
    public const val EXECUTORS_VISIBLE_MIN_ZOOM: Double = 8.0

    // TODO(quality, needs-decision): finding #9 — UZBEKISTAN_CENTER/ANDIJAN_CENTER/BOBUR_SQUARE are
    // Uzbekistan/Fergana product data in a generic SDK and are typed as Pair<Double,Double> (the
    // SDK owns GeoPoint). Moving them to uz.yalla.client.* and/or retyping to GeoPoint is breaking
    // against the committed dumps and the app/iOS consumers. Needs owner sign-off.
    /** Geographic center of Uzbekistan as (lat, lng). */
    public val UZBEKISTAN_CENTER: Pair<Double, Double> = (37.172764 + 45.590075) / 2 to (55.996639 + 73.132278) / 2

    /** Andijan city center as (lat, lng). */
    public val ANDIJAN_CENTER: Pair<Double, Double> = 40.7821 to 72.3442

    /** Bobur Square (Fergana) as (lat, lng). */
    public val BOBUR_SQUARE: Pair<Double, Double> = 40.761746 to 72.351894

    /** Default edge padding used when framing the camera. */
    public val DEFAULT_PADDING: Dp = 60.dp

    // TODO(quality, needs-decision): finding #10 — LIGHT_STYLE_URL duplicates MapStyle.CARTO.lightUrl
    // (two public sources for one value). Consolidating on MapStyle.CARTO is breaking against the
    // committed dumps and the YallaMapsFactory consumers. Needs owner sign-off.
    /** CARTO positron (light) base style URL. */
    public const val LIGHT_STYLE_URL: String = "https://basemaps.cartocdn.com/gl/positron-gl-style/style.json"
}
