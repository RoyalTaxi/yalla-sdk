package uz.yalla.maps.provider.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

/**
 * Compose-state holder tracking the initialization lifecycle of a map composable.
 *
 * Guards against duplicate camera moves by recording which initialization steps
 * have already been performed (map readiness, initial camera positioning,
 * user-location acquisition). Used internally by both Google and Libre map
 * implementations.
 *
 * @see rememberMapInitState
 */
@Stable
internal class MapInitState {
    /**
     * `true` once the underlying platform map has reported it is ready to receive commands.
     */
    var isMapReady by mutableStateOf(false)
        private set

    /**
     * `true` once the map has completed all internal camera initialization logic.
     *
     * After this flag is set, camera-tracking side-effects may begin syncing
     * the marker position from the camera center.
     */
    var isInitialized by mutableStateOf(false)
        private set

    /**
     * `true` once the camera has been moved to **any** initial location (user, fallback, or explicit).
     */
    var hasMovedToLocation by mutableStateOf(false)
        private set

    /**
     * `true` once the camera has been moved specifically to the user's GPS-derived location.
     *
     * This prevents a second animation when a late-arriving location fix comes in
     * after the camera was already placed at the user's position.
     */
    var hasMovedToUserLocation by mutableStateOf(false)
        private set

    /**
     * Marks the underlying map as ready, allowing initialization effects to proceed.
     */
    fun onMapReady() {
        isMapReady = true
    }

    /**
     * Records that the camera has been moved to a location during initialization.
     *
     * @param isUserLocation `true` if the location originates from the device GPS.
     */
    fun onMovedToLocation(isUserLocation: Boolean) {
        hasMovedToLocation = true
        if (isUserLocation) {
            hasMovedToUserLocation = true
        }
    }

    /**
     * Marks initialization as complete, enabling marker-sync side-effects.
     */
    fun onInitialized() {
        isInitialized = true
    }
}

/**
 * Creates and remembers a [MapInitState] for the current composition.
 *
 * @return A remembered [MapInitState] instance.
 */
@Composable
internal fun rememberMapInitState(): MapInitState = remember { MapInitState() }
