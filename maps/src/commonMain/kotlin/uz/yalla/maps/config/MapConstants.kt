package uz.yalla.maps.config

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Generic, cross-platform map tuning constants shared by the Android/iOS renderers. */
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

    /** Default edge padding used when framing the camera. */
    public val DEFAULT_PADDING: Dp = 60.dp
}
