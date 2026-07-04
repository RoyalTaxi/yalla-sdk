package uz.yalla.carto.render.maplibre

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import org.maplibre.compose.camera.CameraMoveReason
import org.maplibre.compose.camera.CameraPosition
import org.maplibre.compose.camera.CameraState
import org.maplibre.compose.camera.rememberCameraState
import org.maplibre.compose.map.GestureOptions
import org.maplibre.compose.map.MapOptions
import org.maplibre.compose.map.MaplibreMap
import org.maplibre.compose.map.OrnamentOptions
import org.maplibre.compose.util.ClickResult
import org.maplibre.spatialk.geojson.Position
import uz.yalla.carto.camera.CameraView
import uz.yalla.carto.state.MapEvent
import uz.yalla.carto.state.MapState

@Composable
public fun MapLibreCartoRenderer(
    state: MapState,
    icons: Map<String, Painter>,
    modifier: Modifier = Modifier
) {
    val isDark by state.isDark.collectAsStateWithLifecycle()
    val style by state.style.collectAsStateWithLifecycle()
    val camera =
        rememberCameraState(
            firstPosition =
                CameraPosition(
                    target = state.initialCamera.target.toPosition(),
                    zoom = state.initialCamera.zoom.toDouble()
                )
        )
    val ready = remember { MutableStateFlow(false) }
    MaplibreMap(
        modifier = modifier,
        baseStyle = baseStyleFor(style, isDark),
        cameraState = camera,
        zoomRange = CameraView.ZOOM_MIN..CameraView.ZOOM_MAX,
        options =
            MapOptions(
                ornamentOptions = OrnamentOptions.AllDisabled,
                gestureOptions = GestureOptions.RotationLocked,
                renderOptions = cartoRenderOptions(state.clippable)
            ),
        onMapClick = { position, _ -> onMapTapped(state, position) },
        onMapLongClick = { position, _ -> onMapLongPressed(state, position) },
        onMapLoadFinished = {
            state.reportReady(true)
            ready.value = true
        }
    ) {
        MapLibreCircles(state)
        MapLibreLines(state)
        MapLibreMarkerLayer(state, icons)
    }
    MapLibreCameraController(state, camera, ready)
    CameraReporter(state, camera)
    DisposableEffect(state) { onDispose { state.reportReady(false) } }
}

@Composable
private fun MapLibreLines(state: MapState) {
    state.lineSources.forEach { source ->
        val routes by source.collectAsStateWithLifecycle()
        routes.forEach { route -> MapLibreLineLayer(route) }
    }
}

@Composable
private fun MapLibreCircles(state: MapState) {
    state.circleSources.forEach { source ->
        val circles by source.collectAsStateWithLifecycle()
        circles.forEach { circle -> MapLibreCircleLayer(circle) }
    }
}

@Composable
private fun CameraReporter(
    state: MapState,
    camera: CameraState
) {
    LaunchedEffect(state, camera) {
        snapshotFlow { CameraSnapshot(camera.isCameraMoving, camera.moveReason, camera.viewOf()) }
            .distinctUntilChanged()
            .collect { report(state, it) }
    }
}

private fun report(
    state: MapState,
    snapshot: CameraSnapshot
) {
    val byUser = snapshot.reason == CameraMoveReason.GESTURE
    state.reportCamera(snapshot.view)
    if (snapshot.moving) {
        state.report(MapEvent.CameraMoved(snapshot.view, byUser))
    } else {
        state.report(MapEvent.CameraIdle(snapshot.view, byUser))
    }
}

private fun CameraState.viewOf(): CameraView =
    CameraView(
        target = position.target.toGeoPoint(),
        zoom = position.zoom.toFloat(),
        bearing = position.bearing.toFloat(),
        tilt = position.tilt.toFloat()
    )

private data class CameraSnapshot(
    val moving: Boolean,
    val reason: CameraMoveReason,
    val view: CameraView
)

private fun onMapTapped(
    state: MapState,
    position: Position
): ClickResult {
    state.report(MapEvent.MapTapped(position.toGeoPoint()))
    return ClickResult.Pass
}

private fun onMapLongPressed(
    state: MapState,
    position: Position
): ClickResult {
    state.report(MapEvent.MapLongPressed(position.toGeoPoint()))
    return ClickResult.Pass
}
