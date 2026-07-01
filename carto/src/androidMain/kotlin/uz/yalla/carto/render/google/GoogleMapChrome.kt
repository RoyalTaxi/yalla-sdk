package uz.yalla.carto.render.google

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.platform.LocalDensity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.CameraMoveStartedReason
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.ComposeMapColorScheme
import com.google.maps.android.compose.MapProperties
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.isActive
import uz.yalla.carto.camera.CameraView
import uz.yalla.carto.render.RECENTER_DURATION_MS
import uz.yalla.carto.state.MapEvent
import uz.yalla.carto.state.MapSink
import uz.yalla.carto.state.MapStyle
import uz.yalla.core.geo.GeoPoint
import kotlin.coroutines.cancellation.CancellationException

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

@Composable
internal fun GooglePaddingRecenter(
    camera: CameraPositionState,
    ready: Boolean,
    padding: PaddingValues
) {
    val bottomPx = with(LocalDensity.current) { padding.calculateBottomPadding().toPx() }
    var anchor by remember { mutableStateOf<LatLng?>(null) }
    LaunchedEffect(camera, ready) {
        if (!ready) return@LaunchedEffect
        snapshotFlow { camera.isMoving }
            .distinctUntilChanged()
            .scan(false to false) { previous, moving -> previous.second to moving }
            .collect { (wasMoving, moving) ->
                if (wasMoving && !moving) anchor = camera.position.target
            }
    }
    var appliedPx by remember { mutableFloatStateOf(bottomPx) }
    LaunchedEffect(bottomPx, ready) {
        if (!ready) return@LaunchedEffect
        if (bottomPx == appliedPx) return@LaunchedEffect
        appliedPx = bottomPx
        val target = anchor ?: return@LaunchedEffect
        try {
            camera.animate(CameraUpdateFactory.newLatLng(target), RECENTER_DURATION_MS)
        } catch (cause: CancellationException) {
            if (!isActive) throw cause
        }
    }
}
