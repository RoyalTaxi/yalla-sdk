package uz.yalla.maps.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import uz.yalla.maps.model.LatLng

/**
 * Mutable state holder for a [Marker] position within the compose-layer map.
 *
 * This is the compose-layer marker state used by [Marker][uz.yalla.maps.compose.Marker].
 * For the API-layer equivalent, see [uz.yalla.maps.api.model.MarkerState].
 *
 * @param position Initial geographic position of the marker.
 * @since 0.0.1
 */
@Stable
class MarkerState(
    position: LatLng = LatLng(0.0, 0.0)
) {
    /**
     * Current geographic position of the marker, observable as Compose state.
     *
     * @since 0.0.1
     */
    var position: LatLng by mutableStateOf(position)
}

/**
 * Creates and remembers a [MarkerState] whose position is kept in sync with the given value.
 *
 * @param position The desired marker position; updates are applied automatically.
 * @return A remembered [MarkerState] tracking the given [position].
 * @since 0.0.1
 */
@Composable
fun rememberUpdatedMarkerState(position: LatLng = LatLng(0.0, 0.0)): MarkerState =
    remember { MarkerState(position) }.also { it.position = position }
