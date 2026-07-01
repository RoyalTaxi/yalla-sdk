package uz.yalla.carto.render.maplibre

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.flow.collect
import org.maplibre.compose.camera.CameraPosition
import org.maplibre.compose.camera.CameraState
import org.maplibre.spatialk.geojson.BoundingBox
import org.maplibre.spatialk.geojson.Position
import uz.yalla.carto.camera.CameraIntent
import uz.yalla.carto.camera.CameraView
import uz.yalla.carto.state.MapState
import uz.yalla.core.geo.GeoPoint
import kotlin.time.Duration.Companion.milliseconds

@Composable
internal fun MapLibreCameraController(
    state: MapState,
    camera: CameraState
) {
    LaunchedEffect(state, camera) {
        state.cameraIntents.collect { intent -> applyIntent(camera, intent) }
    }
}

private suspend fun applyIntent(
    camera: CameraState,
    intent: CameraIntent
) {
    val bounds = intent.bounds
    if (bounds != null && bounds.size >= 2) {
        applyBounds(camera, bounds, intent)
        return
    }
    val target = intent.target ?: return
    val position = positionFor(camera.position, target.toPosition(), intent)
    if (intent.animate) {
        camera.animateTo(position, intent.durationMs.milliseconds)
    } else {
        camera.position = position
    }
}

private suspend fun applyBounds(
    camera: CameraState,
    points: List<GeoPoint>,
    intent: CameraIntent
) {
    val box = boundingBoxOf(points)
    if (intent.animate) {
        camera.animateTo(
            boundingBox = box,
            bearing = camera.position.bearing,
            tilt = camera.position.tilt,
            padding = intent.boundsPadding,
            duration = intent.durationMs.milliseconds
        )
    } else {
        camera.jumpTo(boundingBox = box, padding = intent.boundsPadding)
    }
    val maxZoom = CameraView.FIT_ZOOM_MAX.toDouble()
    if (camera.position.zoom > maxZoom) {
        camera.position = camera.position.copy(zoom = maxZoom)
    }
}

private fun boundingBoxOf(points: List<GeoPoint>): BoundingBox {
    val lats = points.map { it.lat }
    val lngs = points.map { it.lng }
    return BoundingBox(
        southwest = Position(longitude = lngs.min(), latitude = lats.min()),
        northeast = Position(longitude = lngs.max(), latitude = lats.max())
    )
}

private fun positionFor(
    current: CameraPosition,
    target: Position,
    intent: CameraIntent
): CameraPosition =
    current.copy(
        target = target,
        zoom = intent.zoom?.toDouble() ?: current.zoom
    )
