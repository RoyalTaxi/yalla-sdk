package uz.yalla.maps.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import cocoapods.GoogleMaps.GMSCameraPosition
import cocoapods.GoogleMaps.GMSCoordinateBounds
import cocoapods.GoogleMaps.GMSMapView
import cocoapods.GoogleMaps.animateToCameraPosition
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.CoreLocation.CLLocationCoordinate2DMake
import platform.UIKit.UIEdgeInsetsMake
import platform.UIKit.UIScreen
import uz.yalla.maps.model.CameraPosition
import uz.yalla.maps.model.LatLng
import uz.yalla.maps.model.LatLngBounds

/**
 * Sets up bidirectional synchronization between the cross-platform [CameraPositionState]
 * and an iOS [GMSMapView].
 *
 * Animation requests, move requests, and position-update callbacks flow from the
 * cross-platform state to the Google Maps iOS SDK, ensuring the two stay in sync.
 *
 * @param cameraPositionState The cross-platform camera state to synchronize.
 * @param mapView The iOS Google Maps view to drive.
 * @since 0.0.1
 */
@OptIn(ExperimentalForeignApi::class)
@Composable
internal fun SetupCameraPositionStateSync(
    cameraPositionState: CameraPositionState,
    mapView: GMSMapView,
) {
    DisposableEffect(mapView) {
        cameraPositionState.positionUpdater = { position ->
            mapView.camera = position.toGMSCameraPosition()
            cameraPositionState.rawPosition = position
        }
        onDispose { cameraPositionState.positionUpdater = null }
    }

    LaunchedEffect(Unit) {
        cameraPositionState.animationRequests.collect { request ->
            cameraPositionState.cameraMoveStartedReason = CameraMoveStartedReason.DEVELOPER_ANIMATION
            when (request) {
                is CameraAnimationRequest.ToPosition -> {
                    mapView.animateToCameraPosition(request.position.toGMSCameraPosition())
                }
                is CameraAnimationRequest.ToBounds -> {
                    val bounds = request.bounds.toGMSCoordinateBounds()
                    val padding = request.padding.toDouble() / UIScreen.mainScreen.scale
                    val insets = UIEdgeInsetsMake(padding, padding, padding, padding)
                    mapView.cameraForBounds(bounds, insets)?.let { mapView.animateToCameraPosition(it) }
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        cameraPositionState.moveRequests.collect { position ->
            mapView.camera = position.toGMSCameraPosition()
            cameraPositionState.rawPosition = position
        }
    }
}

/**
 * Updates the cross-platform camera state when the iOS map reports the camera is idle.
 *
 * Sets [CameraPositionState.isMoving] to `false` and copies the final position
 * from the GMS camera to the cross-platform state.
 *
 * @param cameraPositionState The cross-platform camera state to update.
 * @param idleAtCameraPosition The GMS camera position at rest.
 * @since 0.0.1
 */
@OptIn(ExperimentalForeignApi::class)
internal fun updateCameraPositionStateOnIdle(
    cameraPositionState: CameraPositionState,
    idleAtCameraPosition: GMSCameraPosition,
) {
    cameraPositionState.isMoving = false
    val (lat, lng) = idleAtCameraPosition.target.useContents { latitude to longitude }
    cameraPositionState.rawPosition =
        CameraPosition(
            target = LatLng(lat, lng),
            zoom = idleAtCameraPosition.zoom,
            bearing = idleAtCameraPosition.bearing.toFloat(),
            tilt = idleAtCameraPosition.viewingAngle.toFloat()
        )
}

/**
 * Updates the cross-platform camera state during an active iOS camera movement.
 *
 * Copies the current GMS camera position (target, zoom, bearing, tilt) into
 * [CameraPositionState.rawPosition] so Compose observers see real-time updates.
 *
 * @param cameraPositionState The cross-platform camera state to update.
 * @param cameraPosition The current GMS camera position during movement.
 * @since 0.0.1
 */
@OptIn(ExperimentalForeignApi::class)
internal fun updateCameraPositionStateOnMove(
    cameraPositionState: CameraPositionState,
    cameraPosition: GMSCameraPosition,
) {
    cameraPosition.target.useContents {
        cameraPositionState.rawPosition =
            CameraPosition(
                target = LatLng(latitude, longitude),
                zoom = cameraPosition.zoom,
                bearing = cameraPosition.bearing.toFloat(),
                tilt = cameraPosition.viewingAngle.toFloat()
            )
    }
}

/**
 * Converts this cross-platform [CameraPosition] to a Google Maps iOS SDK `GMSCameraPosition`.
 *
 * @return A `GMSCameraPosition` with matching target, zoom, bearing, and viewing angle.
 * @since 0.0.1
 */
@OptIn(ExperimentalForeignApi::class)
internal fun CameraPosition.toGMSCameraPosition(): GMSCameraPosition =
    GMSCameraPosition.cameraWithTarget(
        target = CLLocationCoordinate2DMake(target.latitude, target.longitude),
        zoom = zoom,
        bearing = bearing.toDouble(),
        viewingAngle = tilt.toDouble()
    )

/**
 * Converts this [LatLngBounds] to a Google Maps iOS SDK `GMSCoordinateBounds`.
 *
 * @return A `GMSCoordinateBounds` enclosing the same geographic rectangle.
 * @since 0.0.1
 */
@OptIn(ExperimentalForeignApi::class)
internal fun LatLngBounds.toGMSCoordinateBounds(): GMSCoordinateBounds =
    GMSCoordinateBounds()
        .includingCoordinate(CLLocationCoordinate2DMake(southwest.latitude, southwest.longitude))
        .includingCoordinate(CLLocationCoordinate2DMake(northeast.latitude, northeast.longitude))
