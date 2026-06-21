package uz.yalla.maps.api.model

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.LayoutDirection
import uz.yalla.core.geo.GeoPoint
import kotlin.math.abs

/** Default tolerance for latitude/longitude when comparing two camera targets, in degrees. */
public const val CAMERA_LAT_LNG_EPSILON: Double = 1e-6

/** Default tolerance for zoom when comparing two cameras. */
public const val CAMERA_ZOOM_EPSILON: Float = 1e-3f

/** Default tolerance for bearing/tilt (degrees) when comparing two cameras. */
public const val CAMERA_ANGLE_EPSILON: Float = 0.1f

/**
 * Camera-emission de-duplication: returns true when [other] is within the given epsilons of this
 * position (target lat/lng, zoom, bearing, tilt; padding is ignored). The single source of the
 * thresholds the backends use to suppress no-op camera emits during continuous pan/zoom.
 */
public fun CameraPosition.approximatelyEquals(
    other: CameraPosition,
    latLngEpsilon: Double = CAMERA_LAT_LNG_EPSILON,
    zoomEpsilon: Float = CAMERA_ZOOM_EPSILON,
    angleEpsilon: Float = CAMERA_ANGLE_EPSILON
): Boolean =
    abs(target.lat - other.target.lat) < latLngEpsilon &&
        abs(target.lng - other.target.lng) < latLngEpsilon &&
        abs(zoom - other.zoom) < zoomEpsilon &&
        abs(bearing - other.bearing) < angleEpsilon &&
        abs(tilt - other.tilt) < angleEpsilon

/** An animated driver/marker camera target with optional bearing, tilt and edge padding. */
@Immutable
public data class CameraPosition(
    val target: GeoPoint,
    val zoom: Float,
    val bearing: Float = 0f,
    val tilt: Float = 0f,
    val padding: PaddingValues = PaddingValues()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CameraPosition) return false
        return target == other.target &&
            zoom == other.zoom &&
            bearing == other.bearing &&
            tilt == other.tilt &&
            padding.equalsByValue(other.padding)
    }

    override fun hashCode(): Int {
        var result = target.hashCode()
        result = 31 * result + zoom.hashCode()
        result = 31 * result + bearing.hashCode()
        result = 31 * result + tilt.hashCode()
        result = 31 * result + padding.calculateTopPadding().hashCode()
        result = 31 * result + padding.calculateBottomPadding().hashCode()
        result = 31 * result + padding.calculateLeftPadding(LayoutDirection.Ltr).hashCode()
        result = 31 * result + padding.calculateRightPadding(LayoutDirection.Ltr).hashCode()
        return result
    }

    public companion object {
        /** The pre-seed sentinel camera at [GeoPoint.Zero]; treated as "no meaningful camera yet". */
        public val DEFAULT: CameraPosition =
            CameraPosition(
                target = GeoPoint.Zero,
                zoom = 15f
            )
    }
}

private fun PaddingValues.equalsByValue(other: PaddingValues): Boolean {
    return calculateTopPadding() == other.calculateTopPadding() &&
        calculateBottomPadding() == other.calculateBottomPadding() &&
        calculateLeftPadding(LayoutDirection.Ltr) == other.calculateLeftPadding(LayoutDirection.Ltr) &&
        calculateRightPadding(LayoutDirection.Ltr) == other.calculateRightPadding(LayoutDirection.Ltr)
}
