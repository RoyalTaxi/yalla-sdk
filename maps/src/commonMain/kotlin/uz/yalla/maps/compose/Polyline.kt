package uz.yalla.maps.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import uz.yalla.maps.model.Cap
import uz.yalla.maps.model.JointType
import uz.yalla.maps.model.LatLng

/**
 * Platform wrapper around a rendered polyline overlay on the map.
 */
expect class Polyline {
    /**
     * The ordered list of coordinates that define this polyline.
     */
    val points: List<LatLng>
}

/**
 * Draws a polyline overlay connecting the given [points] on the map.
 *
 * Must be called within a [GoogleMap] content lambda. On Android, delegates to
 * `com.google.maps.android.compose.Polyline`; on iOS, creates a `GMSPolyline`.
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
