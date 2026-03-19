package uz.yalla.maps.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import uz.yalla.maps.model.Cap
import uz.yalla.maps.model.JointType
import uz.yalla.maps.model.LatLng

/**
 * Platform wrapper around a rendered polyline overlay on the map.
 *
 * @since 0.0.1
 */
expect class Polyline {
    /**
     * The ordered list of coordinates that define this polyline.
     *
     * @since 0.0.1
     */
    val points: List<LatLng>
}

/**
 * Draws a polyline overlay connecting the given [points] on the map.
 *
 * Must be called within a [GoogleMap] content lambda. On Android, delegates to
 * `com.google.maps.android.compose.Polyline`; on iOS, creates a `GMSPolyline`.
 *
 * @param points Ordered coordinates defining the line path.
 * @param color Stroke color of the polyline.
 * @param endCap Shape drawn at the end of the polyline.
 * @param jointType Style of joints between polyline segments.
 * @param startCap Shape drawn at the start of the polyline.
 * @param width Stroke width in pixels.
 * @since 0.0.1
 */
@Composable
@GoogleMapComposable
expect fun Polyline(
    points: List<LatLng>,
    color: Color = Color.Black,
    endCap: Cap = Cap.Butt,
    jointType: JointType = JointType.Default,
    startCap: Cap = Cap.Butt,
    width: Float = 10f,
)
