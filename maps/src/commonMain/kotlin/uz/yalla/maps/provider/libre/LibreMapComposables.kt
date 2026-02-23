package uz.yalla.maps.provider.libre

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import kotlinx.coroutines.flow.collectLatest
import org.maplibre.compose.camera.CameraMoveReason
import org.maplibre.compose.camera.CameraState
import org.maplibre.compose.map.MapOptions
import org.maplibre.compose.map.MaplibreMap
import org.maplibre.compose.style.BaseStyle
import uz.yalla.core.geo.GeoPoint
import uz.yalla.core.kind.ThemeKind
import uz.yalla.maps.api.MapController
import uz.yalla.maps.api.model.MarkerState
import uz.yalla.maps.config.MapConstants
import uz.yalla.maps.config.platform.getDisabledGestures
import uz.yalla.maps.config.platform.getPlatformGestures
import uz.yalla.maps.config.platform.getPlatformOrnamentOptions
import uz.yalla.maps.config.platform.getPlatformRenderOptions

@Composable
internal fun rememberMapTheme(themeType: ThemeKind): ThemeKind {
    val isSystemDark = isSystemInDarkTheme()
    return remember(themeType, isSystemDark) {
        when (themeType) {
            ThemeKind.Light -> ThemeKind.Light
            ThemeKind.Dark -> ThemeKind.Dark
            ThemeKind.System ->
                if (isSystemDark) ThemeKind.Dark else ThemeKind.Light
        }
    }
}

private fun ThemeKind.toMapStyle(): BaseStyle.Uri =
    BaseStyle.Uri(
        when (this) {
            ThemeKind.Dark -> MapConstants.DARK_STYLE_URL
            else -> MapConstants.LIGHT_STYLE_URL
        }
    )

@Composable
fun BaseMapContent(
    cameraState: CameraState,
    theme: ThemeKind,
    modifier: Modifier = Modifier,
    gesturesEnabled: Boolean = true,
    onMapReady: () -> Unit = {},
    content: @Composable () -> Unit = {}
) {
    val gestureOptions =
        remember(gesturesEnabled) {
            if (gesturesEnabled) getPlatformGestures() else getDisabledGestures()
        }

    MaplibreMap(
        cameraState = cameraState,
        zoomRange = MapConstants.ZOOM_MIN.toFloat()..MapConstants.ZOOM_MAX.toFloat(),
        boundingBox = MapConstants.UZBEKISTAN_BOUNDING_BOX,
        options =
            MapOptions(
                gestureOptions = gestureOptions,
                renderOptions = getPlatformRenderOptions(),
                ornamentOptions = getPlatformOrnamentOptions()
            ),
        onMapLoadFinished = onMapReady,
        baseStyle = theme.toMapStyle(),
        modifier = modifier,
        content = content
    )
}

@Composable
fun CameraTrackingEffect(
    cameraState: CameraState,
    controller: LibreMapController,
    onMarkerChanged: ((MarkerState) -> Unit)?
) {
    LaunchedEffect(Unit) {
        snapshotFlow { cameraState.isCameraMoving }
            .collectLatest { isMoving ->
                val isByUser = cameraState.moveReason == CameraMoveReason.GESTURE
                val suppressMarkerUpdate =
                    controller.shouldSuppressMarkerUpdate(
                        isMoving = isMoving,
                        isByUser = isByUser
                    )

                if (isByUser) {
                    controller.onUserGesture()
                }

                if (!isMoving) {
                    controller.onCameraIdle()
                }

                val target = cameraState.position.target
                val state =
                    MarkerState(
                        point = GeoPoint(target.latitude, target.longitude),
                        isMoving = isMoving,
                        isByUser = isByUser
                    )
                controller.updateFromCamera(cameraState.position)
                if (!suppressMarkerUpdate) {
                    controller.updateMarkerState(state)
                    onMarkerChanged?.invoke(state)
                }
            }
    }
}

fun MapController.requireLibreController(): LibreMapController =
    when (this) {
        is LibreMapController -> this
        is uz.yalla.maps.provider.SwitchingMapController -> libreController
        else -> error("Expected LibreMapController or SwitchingMapController, got ${this::class.simpleName}")
    }
