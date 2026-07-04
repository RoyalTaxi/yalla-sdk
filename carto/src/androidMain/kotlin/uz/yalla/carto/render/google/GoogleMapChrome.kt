package uz.yalla.carto.render.google

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.CameraMoveStartedReason
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.ComposeMapColorScheme
import com.google.maps.android.compose.MapProperties
import uz.yalla.carto.camera.CameraView
import uz.yalla.carto.state.MapEvent
import uz.yalla.carto.state.MapSink
import uz.yalla.carto.state.MapStyle
import uz.yalla.core.geo.GeoPoint

internal fun mapPropertiesOf(
    style: MapStyle,
    isDark: Boolean
): MapProperties =
    MapProperties(
        mapStyleOptions = styleOptionsOf(style, isDark),
        minZoomPreference = CameraView.ZOOM_MIN,
        maxZoomPreference = CameraView.ZOOM_MAX,
        isBuildingEnabled = false
    )

private fun styleOptionsOf(
    style: MapStyle,
    isDark: Boolean
): MapStyleOptions? =
    when (style) {
        is MapStyle.InlineJson -> MapStyleOptions(if (isDark) style.darkJson else style.lightJson)
        else -> null
    }

internal fun colorSchemeOf(isDark: Boolean): ComposeMapColorScheme =
    if (isDark) ComposeMapColorScheme.DARK else ComposeMapColorScheme.LIGHT

@Composable
internal fun GoogleCameraReporter(
    camera: CameraPositionState,
    sink: MapSink
) {
    LaunchedEffect(camera) {
        snapshotFlow { camera.isMoving to camera.cameraMoveStartedReason }
            .collect { (moving, reason) -> sink.reportMove(camera, moving, isUser(reason)) }
    }
}

private fun MapSink.reportMove(
    camera: CameraPositionState,
    moving: Boolean,
    byUser: Boolean
) {
    val view = camera.position.toCameraView()
    reportCamera(view)
    if (moving) {
        report(MapEvent.CameraMoved(view, byUser))
    } else {
        report(MapEvent.CameraIdle(view, byUser))
    }
}

private fun isUser(reason: CameraMoveStartedReason): Boolean = reason == CameraMoveStartedReason.GESTURE

private fun com.google.android.gms.maps.model.CameraPosition.toCameraView(): CameraView =
    CameraView(GeoPoint(target.latitude, target.longitude), zoom, bearing, tilt)
