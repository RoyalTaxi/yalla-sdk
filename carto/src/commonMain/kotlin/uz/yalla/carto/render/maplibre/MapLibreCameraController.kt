package uz.yalla.carto.render.maplibre

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import org.maplibre.compose.camera.CameraMoveReason
import org.maplibre.compose.camera.CameraPosition
import org.maplibre.compose.camera.CameraState
import org.maplibre.spatialk.geojson.BoundingBox
import org.maplibre.spatialk.geojson.Position
import uz.yalla.carto.camera.CameraIntent
import uz.yalla.carto.camera.CameraView
import uz.yalla.carto.render.RECENTER_DURATION_MS
import uz.yalla.carto.state.MapState
import uz.yalla.core.geo.GeoPoint
import kotlin.time.Duration.Companion.milliseconds

@Composable
internal fun MapLibreCameraController(
    state: MapState,
    camera: CameraState,
    ready: StateFlow<Boolean>
) {
    val isReady by ready.collectAsStateWithLifecycle()
    val padding by state.padding.collectAsStateWithLifecycle()
    val layoutDirection = LocalLayoutDirection.current
    val viewportPadding = padding.toEdges(layoutDirection)
    var focus by remember(camera) { mutableStateOf<MapLibreViewportFocus?>(null) }

    LaunchedEffect(camera) {
        var movedByUser = false
        snapshotFlow { camera.isCameraMoving to camera.moveReason }
            .distinctUntilChanged()
            .collect { (moving, reason) ->
                if (moving && reason == CameraMoveReason.GESTURE) movedByUser = true
                if (!moving && movedByUser) {
                    focus = MapLibreViewportFocus.Free
                    movedByUser = false
                }
            }
    }

    LaunchedEffect(state, camera, viewportPadding, layoutDirection) {
        state.cameraIntents.collect { intent ->
            focus = intent.toMapLibreViewportFocus() ?: focus
            applyIntent(camera, intent, viewportPadding, layoutDirection)
        }
    }

    var appliedPadding by remember(camera) { mutableStateOf<PaddingEdges?>(null) }
    LaunchedEffect(viewportPadding, isReady, layoutDirection) {
        if (!isReady) return@LaunchedEffect
        val animate = appliedPadding != null
        if (viewportPadding == appliedPadding) return@LaunchedEffect
        appliedPadding = viewportPadding
        when (val current = focus) {
            is MapLibreViewportFocus.Intent ->
                applyIntent(
                    camera = camera,
                    intent =
                        current.intent.copy(
                            animate = animate,
                            durationMs = RECENTER_DURATION_MS
                        ),
                    viewportPadding = viewportPadding,
                    layoutDirection = layoutDirection
                )

            MapLibreViewportFocus.Free,
            null -> {
                val next = camera.position.copy(padding = viewportPadding.toPaddingValues())
                if (animate) {
                    camera.animateTo(next, RECENTER_DURATION_MS.milliseconds)
                } else {
                    camera.position = next
                }
            }
        }
    }
}

private sealed interface MapLibreViewportFocus {
    data class Intent(
        val intent: CameraIntent
    ) : MapLibreViewportFocus

    data object Free : MapLibreViewportFocus
}

private fun CameraIntent.toMapLibreViewportFocus(): MapLibreViewportFocus? =
    when {
        isBoundsIntent || target != null -> MapLibreViewportFocus.Intent(this)
        else -> null
    }

private suspend fun applyIntent(
    camera: CameraState,
    intent: CameraIntent,
    viewportPadding: PaddingEdges,
    layoutDirection: LayoutDirection
) {
    val bounds = intent.bounds
    if (bounds != null && bounds.size >= 2) {
        applyBounds(camera, bounds, intent, viewportPadding, layoutDirection)
        return
    }
    val target = intent.target ?: return
    val position = positionFor(camera.position, target.toPosition(), intent, viewportPadding)
    if (intent.animate) {
        camera.animateTo(position, intent.durationMs.milliseconds)
    } else {
        camera.position = position
    }
}

private suspend fun applyBounds(
    camera: CameraState,
    points: List<GeoPoint>,
    intent: CameraIntent,
    viewportPadding: PaddingEdges,
    layoutDirection: LayoutDirection
) {
    val box = boundingBoxOf(points)
    val padding = viewportPadding.plus(intent.boundsPadding, layoutDirection).toPaddingValues()
    if (intent.animate) {
        camera.animateTo(
            boundingBox = box,
            bearing = camera.position.bearing,
            tilt = camera.position.tilt,
            padding = padding,
            duration = intent.durationMs.milliseconds
        )
    } else {
        camera.jumpTo(boundingBox = box, padding = padding)
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
    intent: CameraIntent,
    viewportPadding: PaddingEdges
): CameraPosition =
    current.copy(
        target = target,
        zoom = intent.zoom?.toDouble() ?: current.zoom,
        padding = viewportPadding.toPaddingValues()
    )

private val CameraIntent.isBoundsIntent: Boolean get() = (bounds?.size ?: 0) >= 2

private data class PaddingEdges(
    val start: Dp,
    val top: Dp,
    val end: Dp,
    val bottom: Dp
) {
    fun plus(
        other: PaddingValues,
        layoutDirection: LayoutDirection
    ): PaddingEdges =
        PaddingEdges(
            start = start + other.calculateLeftPadding(layoutDirection),
            top = top + other.calculateTopPadding(),
            end = end + other.calculateRightPadding(layoutDirection),
            bottom = bottom + other.calculateBottomPadding()
        )

    fun toPaddingValues(): PaddingValues =
        PaddingValues(
            start = start,
            top = top,
            end = end,
            bottom = bottom
        )
}

private fun PaddingValues.toEdges(layoutDirection: LayoutDirection): PaddingEdges =
    PaddingEdges(
        start = calculateLeftPadding(layoutDirection),
        top = calculateTopPadding(),
        end = calculateRightPadding(layoutDirection),
        bottom = calculateBottomPadding()
    )
