package uz.yalla.maps.api.model

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.LayoutDirection
import uz.yalla.core.geo.GeoPoint
import uz.yalla.maps.util.hasSameValues

/**
 * Immutable snapshot of the map camera position used by [MapController].
 *
 * Represents the geographic target, zoom level, bearing, tilt, and content padding.
 * Custom [equals] and [hashCode] implementations account for [PaddingValues] comparison.
 *
 * @property target The geographic coordinate the camera is centered on.
 * @property zoom The zoom level (typically 0..21).
 * @property bearing Camera heading in degrees clockwise from north.
 * @property tilt Camera tilt angle from nadir in degrees (0 = straight down).
 * @property padding Content padding applied to the map viewport.
 * @since 0.0.1
 */
@Immutable
data class CameraPosition(
    val target: GeoPoint,
    val zoom: Float,
    val bearing: Float = 0f,
    val tilt: Float = 0f,
    val padding: PaddingValues = PaddingValues()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CameraPosition) return false
        return target == other.target && zoom == other.zoom &&
            bearing == other.bearing && tilt == other.tilt &&
            padding.hasSameValues(other.padding)
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

    companion object {
        /**
         * Default camera position centered at [GeoPoint.Zero] with zoom 15.
         *
         * @since 0.0.1
         */
        val DEFAULT =
            CameraPosition(
                target = GeoPoint.Zero,
                zoom = 15f
            )
    }
}
