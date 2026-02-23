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

@OptIn(ExperimentalForeignApi::class)
internal fun CameraPosition.toGMSCameraPosition(): GMSCameraPosition =
    GMSCameraPosition.cameraWithTarget(
        target = CLLocationCoordinate2DMake(target.latitude, target.longitude),
        zoom = zoom,
        bearing = bearing.toDouble(),
        viewingAngle = tilt.toDouble()
    )

@OptIn(ExperimentalForeignApi::class)
internal fun LatLngBounds.toGMSCoordinateBounds(): GMSCoordinateBounds =
    GMSCoordinateBounds()
        .includingCoordinate(CLLocationCoordinate2DMake(southwest.latitude, southwest.longitude))
        .includingCoordinate(CLLocationCoordinate2DMake(northeast.latitude, northeast.longitude))
