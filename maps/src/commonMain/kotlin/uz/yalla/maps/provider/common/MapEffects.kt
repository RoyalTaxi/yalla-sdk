package uz.yalla.maps.provider.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import uz.yalla.core.contract.LastLocationProvider
import uz.yalla.core.contract.LocationProvider
import uz.yalla.core.geo.GeoPoint
import uz.yalla.maps.api.MapController
import uz.yalla.maps.api.model.MarkerState
import uz.yalla.maps.config.MapConstants

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

@Composable
internal fun CameraInitializationEffect(
    pendingTarget: GeoPoint?,
    userLocation: GeoPoint?,
    hasCachedLocation: Boolean,
    controller: MapController,
    lastLocationProvider: LastLocationProvider?,
    onInitialized: (isUserLocation: Boolean) -> Unit,
    onMarkerChanged: ((MarkerState) -> Unit)?,
    onMapReady: (() -> Unit)?
) {
    LaunchedEffect(pendingTarget) {
        pendingTarget ?: return@LaunchedEffect

        controller.moveTo(pendingTarget, MapConstants.DEFAULT_ZOOM.toFloat())
        notifyMarkerChanged(controller, pendingTarget, onMarkerChanged)

        val isUserLocation = userLocation != null && userLocation == pendingTarget
        if (isUserLocation) {
            lastLocationProvider?.setLastLocation(pendingTarget)
        }

        val isValidInitialPosition = isUserLocation || (userLocation == null && hasCachedLocation)
        if (isValidInitialPosition) {
            onMapReady?.invoke()
        }

        onInitialized(isUserLocation)
    }
}

internal fun notifyMarkerChanged(
    controller: MapController,
    point: GeoPoint,
    onMarkerChanged: ((MarkerState) -> Unit)?
) {
    val state = MarkerState(point, isMoving = false, isByUser = false)
    controller.updateMarkerState(state)
    onMarkerChanged?.invoke(state)
}
