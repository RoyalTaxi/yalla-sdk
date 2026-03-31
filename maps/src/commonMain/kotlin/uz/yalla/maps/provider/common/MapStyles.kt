package uz.yalla.maps.provider.common

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Color tokens used by map overlay components (markers, routes, badges).
 *
 * @since 0.0.1
 */
object MapColors {
    /** Primary brand purple for routes and start markers. */
    val Primary = Color(0xFF562DF8)

    /** Red color for the finish/destination marker. */
    val FinishMarker = Color(0xFFFF3B30)

    /** Gray color for intermediate waypoint markers. */
    val IntermediateMarker = Color(0xFFAEAEB2)

    /** Dark background for marker label badges. */
    val BadgeBackground = Color(0xFF1C1C1E)

    /** White text color for badge labels. */
    val BadgeText = Color.White

    /** White fill for the inner area of location markers. */
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
