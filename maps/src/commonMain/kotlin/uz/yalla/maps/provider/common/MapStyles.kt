package uz.yalla.maps.provider.common

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Color tokens used by map overlay components (markers, routes, badges).
 *
 * These values are **intentionally hardcoded** rather than referencing `yalla-design` tokens
 * because the maps module has no dependency on the design module. Map overlays are rendered
 * by the native map SDKs (Google Maps, MapLibre) which operate outside the Compose theme,
 * so design-system tokens would not propagate correctly.
 *
 * If the brand palette changes, update these values manually to stay in sync.
 *
 * @since 0.0.1
 */
object MapColors {
    /** Primary brand purple (`#562DF8`) used for route polylines and start/origin markers. */
    val Primary = Color(0xFF562DF8)

    /** iOS system red (`#FF3B30`) used for the finish/destination marker. */
    val FinishMarker = Color(0xFFFF3B30)

    /** iOS system gray (`#AEAEB2`) used for intermediate waypoint markers. */
    val IntermediateMarker = Color(0xFFAEAEB2)

    /** Near-black (`#1C1C1E`) background for label badge pills above markers. */
    val BadgeBackground = Color(0xFF1C1C1E)

    /** White text inside label badge pills. */
    val BadgeText = Color.White

    /** White fill for the inner circle of location markers, providing contrast against the colored border. */
    val MarkerFill = Color.White
}

/**
 * Dimension tokens used by map overlay components.
 *
 * @since 0.0.1
 */
object MapDimens {
    /** Outer diameter of location marker circles. */
    val MarkerSize = 22.dp

    /** Outer diameter of user location indicator dot. */
    val UserLocationSize = 16.dp

    /** Border width of location marker circles. */
    val MarkerBorderWidth = 6.dp

    /** Height of label badge pills above markers. */
    val BadgeHeight = 28.dp

    /** Horizontal padding inside label badge pills. */
    val BadgePadding = 12.dp

    /** Stroke width of route polylines. */
    val RouteWidth = 4.dp

    /** Default GPS accuracy radius in meters for the location indicator. */
    const val DEFAULT_ACCURACY_METERS = 50.0

    /** Base size of the accuracy circle painter in pixels. */
    const val ACCURACY_LAYER_SIZE = 256f
}

/**
 * Classification of a location marker within a route.
 *
 * @since 0.0.1
 */
enum class LocationType {
    /** Origin/pickup point. */
    START,

    /** Intermediate waypoint. */
    POINT,

    /** Destination/drop-off point. */
    FINISH
}
