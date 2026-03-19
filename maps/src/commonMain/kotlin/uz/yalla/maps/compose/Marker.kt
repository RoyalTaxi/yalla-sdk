package uz.yalla.maps.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset

/**
 * Places a marker overlay on the map at the position defined by [state].
 *
 * Must be called within a [GoogleMap] content lambda. On Android, delegates to
 * `com.google.maps.android.compose.Marker`; on iOS, creates a `GMSMarker`.
 *
 * @param state Mutable position state for the marker.
 * @param icon Optional custom bitmap icon; `null` uses the platform default pin.
 * @param anchor Normalized anchor point within the icon (0,0 = top-left, 1,1 = bottom-right).
 * @param flat Whether the marker is rendered flat against the map surface.
 * @param rotation Clockwise rotation of the marker in degrees.
 * @since 0.0.1
 */
@Composable
@GoogleMapComposable
expect fun Marker(
    state: MarkerState = rememberUpdatedMarkerState(),
    icon: BitmapDescriptor? = null,
    anchor: Offset = Offset(0.5f, 1.0f),
    flat: Boolean = false,
    rotation: Float = 0.0f,
)
