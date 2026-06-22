package uz.yalla.maps.api.model

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.LayoutDirection
import uz.yalla.core.geo.GeoPoint
import kotlin.math.abs

public const val CAMERA_LAT_LNG_EPSILON: Double = 1e-6

public const val CAMERA_ZOOM_EPSILON: Float = 1e-3f

public const val CAMERA_ANGLE_EPSILON: Float = 0.1f

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
