package uz.yalla.maps.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import uz.yalla.maps.model.LatLng

/**
 * Platform wrapper around a rendered circle overlay on the map.
 *
 * @since 0.0.1
 */
expect class Circle {
    /**
     * Center coordinate of the circle.
     *
     * @since 0.0.1
     */
    val center: LatLng

    /**
     * Radius of the circle in meters.
     *
     * @since 0.0.1
     */
    val radius: Double
}

/**
 * Draws a circle overlay on the map centered at [center] with the given [radius].
 *
 * Must be called within a [GoogleMap] content lambda. On Android, delegates to
 * `com.google.maps.android.compose.Circle`; on iOS, creates a `GMSCircle`.
 *
 * @param center Geographic center of the circle.
 * @param radius Radius in meters.
 * @param fillColor Interior fill color.
 * @param strokeColor Border stroke color.
 * @param strokeWidth Border stroke width in pixels.
 * @since 0.0.1
 */
@Composable
@GoogleMapComposable
expect fun Circle(
    center: LatLng,
    radius: Double = 0.0,
    fillColor: Color = Color.Transparent,
    strokeColor: Color = Color.Black,
    strokeWidth: Float = 10f,
)
