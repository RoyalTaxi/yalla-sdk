package uz.yalla.maps.provider.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import uz.yalla.core.geo.GeoPoint
import uz.yalla.core.location.LocationProvider
import uz.yalla.maps.api.MapController
import uz.yalla.maps.api.model.MarkerState
import uz.yalla.maps.config.MapConstants

/**
 * Side-effect that starts location tracking when the user has granted permission.
 *
 * Calls [LocationProvider.startTracking] each time [hasPermission] transitions to `true`.
 * When the permission is revoked, the provider stops on its own — no explicit stop call
 * is needed here.
 */
@Composable
internal fun LocationTrackingEffect(
    locationProvider: LocationProvider,
    hasPermission: Boolean
) {
    LaunchedEffect(hasPermission) {
        if (hasPermission) {
            locationProvider.startTracking()
        }
    }
}

/**
 * Side-effect that performs initial camera placement when a [pendingTarget] becomes available.
 *
 * Moves the camera to [pendingTarget] at [MapConstants.DEFAULT_ZOOM], notifies the marker
 * callback, and optionally fires [onMapReady] if the target represents a valid initial
 * position (user location or cached location).
 *
 * @param userLocation Current user GPS location, used to determine if the target is user-sourced.
 * @param onMapReady Optional callback invoked when the initial position is a valid starting state.
 */
@Composable
internal fun CameraInitializationEffect(
    pendingTarget: GeoPoint?,
    userLocation: GeoPoint?,
    hasCachedLocation: Boolean,
    controller: MapController,
    onInitialized: (isUserLocation: Boolean) -> Unit,
    onMarkerChanged: ((MarkerState) -> Unit)?,
    onMapReady: (() -> Unit)?
) {
    LaunchedEffect(pendingTarget) {
        pendingTarget ?: return@LaunchedEffect

        controller.moveTo(pendingTarget, MapConstants.DEFAULT_ZOOM.toFloat())
        notifyMarkerChanged(controller, pendingTarget, onMarkerChanged)

        val isUserLocation = userLocation != null && userLocation == pendingTarget
        val isValidInitialPosition = isUserLocation || (userLocation == null && hasCachedLocation)
        if (isValidInitialPosition) {
            onMapReady?.invoke()
        }

        onInitialized(isUserLocation)
    }
}

/**
 * Updates the controller's marker state and invokes the optional callback.
 *
 * Creates a [MarkerState] at [point] with `isMoving = false` and `isByUser = false`,
 * pushes it to the [controller], and forwards it to [onMarkerChanged].
 */
internal fun notifyMarkerChanged(
    controller: MapController,
    point: GeoPoint,
    onMarkerChanged: ((MarkerState) -> Unit)?
) {
    val state = MarkerState(point, isMoving = false, isByUser = false)
    controller.updateMarkerState(state)
    onMarkerChanged?.invoke(state)
}
