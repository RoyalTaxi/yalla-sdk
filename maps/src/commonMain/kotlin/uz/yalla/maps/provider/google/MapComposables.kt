package uz.yalla.maps.provider.google

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.IntSize
import kotlinx.coroutines.flow.collectLatest
import uz.yalla.core.geo.GeoPoint
import uz.yalla.core.kind.ThemeKind
import uz.yalla.maps.api.MapController
import uz.yalla.maps.api.model.MarkerState
import uz.yalla.maps.compose.CameraMoveStartedReason
import uz.yalla.maps.compose.CameraPositionState
import uz.yalla.maps.compose.GoogleMap
import uz.yalla.maps.compose.GoogleMapComposable
import uz.yalla.maps.config.MapConstants
import uz.yalla.maps.model.CameraPosition
import uz.yalla.maps.model.MapProperties
import uz.yalla.maps.model.MapUiSettings

private val DefaultUiSettings =
    MapUiSettings(
        compassEnabled = false,
        indoorLevelPickerEnabled = false,
        mapToolbarEnabled = false,
        myLocationButtonEnabled = false,
        rotationGesturesEnabled = false,
        scrollGesturesEnabled = true,
        scrollGesturesEnabledDuringRotateOrZoom = true,
        tiltGesturesEnabled = false,
        zoomControlsEnabled = false,
        zoomGesturesEnabled = true
    )

private val DisabledGesturesUiSettings =
    DefaultUiSettings.copy(
        scrollGesturesEnabled = false,
        scrollGesturesEnabledDuringRotateOrZoom = false,
        zoomGesturesEnabled = false
    )

private val DefaultProperties =
    MapProperties(
        isBuildingEnabled = false,
        isIndoorEnabled = false,
        isMyLocationEnabled = false,
        isTrafficEnabled = false,
        minZoomPreference = MapConstants.ZOOM_MIN.toFloat(),
        maxZoomPreference = MapConstants.ZOOM_MAX.toFloat()
    )

@Composable
fun BaseMapContent(
    cameraState: CameraPositionState,
    theme: ThemeKind,
    modifier: Modifier = Modifier,
    gesturesEnabled: Boolean = true,
    contentPadding: PaddingValues,
    onMapSizeChanged: ((IntSize) -> Unit)? = null,
    onMapReady: () -> Unit = {},
    content:
        @Composable
        @GoogleMapComposable () -> Unit = {},
) {
    val uiSettings =
        remember(gesturesEnabled) {
            if (gesturesEnabled) DefaultUiSettings else DisabledGesturesUiSettings
        }

    val mapModifier =
        if (onMapSizeChanged != null) {
            modifier.onSizeChanged(onMapSizeChanged)
        } else {
            modifier
        }

    GoogleMap(
        modifier = mapModifier,
        cameraPositionState = cameraState,
        properties = DefaultProperties,
        uiSettings = uiSettings,
        theme = theme,
        contentPadding = contentPadding,
        onMapLoaded = onMapReady,
        content = content
    )
}

@Composable
internal fun rememberAnimatedPadding(target: PaddingValues): PaddingValues {
    val layoutDirection = LocalLayoutDirection.current
    val animationSpec = tween<androidx.compose.ui.unit.Dp>(durationMillis = PADDING_ANIMATION_MS)

    val start by animateDpAsState(
        targetValue = target.calculateLeftPadding(layoutDirection),
        animationSpec = animationSpec,
        label = "mapPaddingStart"
    )
    val end by animateDpAsState(
        targetValue = target.calculateRightPadding(layoutDirection),
        animationSpec = animationSpec,
        label = "mapPaddingEnd"
    )
    val top by animateDpAsState(
        targetValue = target.calculateTopPadding(),
        animationSpec = animationSpec,
        label = "mapPaddingTop"
    )
    val bottom by animateDpAsState(
        targetValue = target.calculateBottomPadding(),
        animationSpec = animationSpec,
        label = "mapPaddingBottom"
    )

    return PaddingValues(start = start, top = top, end = end, bottom = bottom)
}

@Composable
fun CameraTrackingEffect(
    cameraState: CameraPositionState,
    controller: GoogleMapController,
    onMarkerChanged: ((MarkerState) -> Unit)?
) {
    LaunchedEffect(Unit) {
        snapshotFlow { cameraState.isMoving }
            .collectLatest { isMoving ->
                val isByUser = isMoving && cameraState.cameraMoveStartedReason == CameraMoveStartedReason.GESTURE

                // When user manually moves the map, clear intended target
                if (isByUser) {
                    controller.onUserGesture()
                }

                if (!isMoving) {
                    controller.onCameraIdle()
                }

                val state =
                    MarkerState(
                        point = cameraState.position.target.toGeoPoint(),
                        isMoving = isMoving,
                        isByUser = isByUser
                    )
                controller.updateMarkerState(state)
                controller.updateFromCamera(cameraState.position)
                onMarkerChanged?.invoke(state)
            }
    }
}

@Composable
internal fun PreMapCameraPositionEffect(
    isMapReady: Boolean,
    target: GeoPoint,
    cameraState: CameraPositionState
) {
    LaunchedEffect(isMapReady, target) {
        if (!isMapReady) {
            cameraState.position =
                CameraPosition(
                    target = target.toLatLng(),
                    zoom = MapConstants.DEFAULT_ZOOM.toFloat()
                )
        }
    }
}

private const val PADDING_ANIMATION_MS = MapController.ANIMATION_DURATION
