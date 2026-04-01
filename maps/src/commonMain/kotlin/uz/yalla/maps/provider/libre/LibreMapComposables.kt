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
import uz.yalla.core.settings.ThemeKind
import uz.yalla.maps.api.MapController
import uz.yalla.maps.api.model.MarkerState
import uz.yalla.maps.config.MapConstants
import uz.yalla.maps.config.platform.getDisabledGestures
import uz.yalla.maps.config.platform.getPlatformGestures
import uz.yalla.maps.config.platform.getPlatformOrnamentOptions
import uz.yalla.maps.config.platform.getPlatformRenderOptions

/**
 * Resolves the effective [ThemeKind] for MapLibre, converting [ThemeKind.System]
 * to [ThemeKind.Light] or [ThemeKind.Dark] based on the current system appearance.
 *
 * @param themeType The user's theme preference.
 * @return A resolved theme that is always either [ThemeKind.Light] or [ThemeKind.Dark].
 * @since 0.0.1
 */
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

/**
 * Converts this [ThemeKind] to the corresponding MapLibre [BaseStyle.Uri].
 */
private fun ThemeKind.toMapStyle(): BaseStyle.Uri =
    BaseStyle.Uri(
        when (this) {
            ThemeKind.Dark -> MapConstants.DARK_STYLE_URL
            else -> MapConstants.LIGHT_STYLE_URL
        }
    )

/**
 * Shared base map composable for all MapLibre implementations.
 *
 * Wraps [MaplibreMap] with standard Yalla UI settings (ornaments off, gestures configured)
 * and Uzbekistan bounding box. Used internally by [LibreLiteMap], [LibreExtendedMap],
 * and [LibreStaticMap].
 *
 * @param cameraState MapLibre camera state driving the viewport.
 * @param theme Resolved color scheme (light or dark).
 * @param modifier Compose [Modifier] applied to the map.
 * @param gesturesEnabled Whether scroll and zoom gestures are active.
 * @param onMapReady Callback invoked when tiles finish loading.
 * @param content Optional overlay composable content.
 * @since 0.0.1
 */
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

/**
 * Side-effect that tracks MapLibre camera movement and syncs the [controller].
 *
 * Observes [CameraState.isCameraMoving] and forwards idle/gesture events to
 * [LibreMapController], keeping marker state and camera position in sync.
 *
 * @param cameraState The MapLibre camera state to observe.
 * @param controller The MapLibre controller to update.
 * @param onMarkerChanged Optional callback invoked on marker position changes.
 * @since 0.0.1
 */
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

/**
 * Extracts the [LibreMapController] from this controller or its [SwitchingMapController] wrapper.
 *
 * @return The underlying [LibreMapController].
 * @throws IllegalStateException if this is neither a [LibreMapController] nor a [SwitchingMapController].
 * @since 0.0.1
 */
fun MapController.requireLibreController(): LibreMapController =
    when (this) {
        is LibreMapController -> this
        is uz.yalla.maps.provider.SwitchingMapController -> libreController
        else -> error("Expected LibreMapController or SwitchingMapController, got ${this::class.simpleName}")
    }
