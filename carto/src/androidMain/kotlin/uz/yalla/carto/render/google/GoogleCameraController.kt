package uz.yalla.carto.render.google

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.CameraMoveStartedReason
import com.google.maps.android.compose.CameraPositionState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import uz.yalla.carto.camera.CameraIntent
import uz.yalla.carto.camera.CameraView
import uz.yalla.carto.render.RECENTER_DURATION_MS
import uz.yalla.core.geo.GeoPoint

@Composable
internal fun GoogleCameraController(
    cameraPositionState: CameraPositionState,
    intents: SharedFlow<CameraIntent>,
    ready: StateFlow<Boolean>,
    padding: PaddingValues
) {
    val density = LocalDensity.current
    val layoutDirection = LocalLayoutDirection.current
    val isReady by ready.collectAsStateWithLifecycle()
    val paddingPx = padding.toPx(density, layoutDirection)
    var focus by remember(cameraPositionState) { mutableStateOf<GoogleViewportFocus?>(null) }

    LaunchedEffect(cameraPositionState, isReady) {
        if (!isReady) return@LaunchedEffect
        var movedByUser = false
        snapshotFlow { cameraPositionState.isMoving to cameraPositionState.cameraMoveStartedReason }
            .distinctUntilChanged()
            .collect { (moving, reason) ->
                if (moving && reason == CameraMoveStartedReason.GESTURE) movedByUser = true
                if (!moving && movedByUser) {
                    focus = GoogleViewportFocus.Free(cameraPositionState.position.target)
                    movedByUser = false
                }
            }
    }

    LaunchedEffect(cameraPositionState, intents, ready, density, layoutDirection) {
        var job: Job? = null
        intents.collect { intent ->
            focus = intent.toGoogleViewportFocus() ?: focus
            job?.cancel()
            job =
                launch {
                    ready.first { it }
                    applyIntent(cameraPositionState, intent, density, layoutDirection)
                }
        }
    }

    var appliedPaddingPx by remember(cameraPositionState) { mutableStateOf(paddingPx) }
    LaunchedEffect(paddingPx, isReady, density, layoutDirection) {
        if (!isReady) return@LaunchedEffect
        if (paddingPx == appliedPaddingPx) return@LaunchedEffect
        appliedPaddingPx = paddingPx
        when (val current = focus) {
            is GoogleViewportFocus.Intent ->
                applyIntent(
                    cameraPositionState = cameraPositionState,
                    intent =
                        current.intent.copy(
                            animate = true,
                            durationMs = RECENTER_DURATION_MS
                        ),
                    density = density,
                    layoutDirection = layoutDirection
                )

            is GoogleViewportFocus.Free ->
                runCatching {
                    cameraPositionState.animate(
                        CameraUpdateFactory.newLatLng(current.target),
                        RECENTER_DURATION_MS
                    )
                }

            null -> Unit
        }
    }
}

private sealed interface GoogleViewportFocus {
    data class Intent(
        val intent: CameraIntent
    ) : GoogleViewportFocus

    data class Free(
        val target: LatLng
    ) : GoogleViewportFocus
}

private fun CameraIntent.toGoogleViewportFocus(): GoogleViewportFocus? =
    when {
        isBoundsIntent || target != null -> GoogleViewportFocus.Intent(this)
        else -> null
    }

private suspend fun applyIntent(
    cameraPositionState: CameraPositionState,
    intent: CameraIntent,
    density: Density,
    layoutDirection: LayoutDirection
) {
    val update = updateFor(intent, cameraPositionState, density, layoutDirection) ?: return
    runCatching {
        if (intent.animate) {
            cameraPositionState.animate(update, intent.durationMs)
        } else {
            cameraPositionState.move(update)
        }
        if (intent.isBoundsIntent && cameraPositionState.position.zoom > CameraView.FIT_ZOOM_MAX) {
            cameraPositionState.move(CameraUpdateFactory.zoomTo(CameraView.FIT_ZOOM_MAX))
        }
    }
}

private fun updateFor(
    intent: CameraIntent,
    camera: CameraPositionState,
    density: Density,
    layoutDirection: LayoutDirection
): CameraUpdate? {
    val bounds = intent.bounds
    if (bounds != null && bounds.size >= 2) {
        return CameraUpdateFactory.newLatLngBounds(
            boundsOf(bounds),
            boundsPaddingPx(intent.boundsPadding, density, layoutDirection)
        )
    }
    val target = intent.target ?: return null
    return CameraUpdateFactory.newCameraPosition(positionFor(intent, target, camera))
}

private fun boundsOf(points: List<GeoPoint>): LatLngBounds {
    val builder = LatLngBounds.Builder()
    points.forEach { builder.include(LatLng(it.lat, it.lng)) }
    return builder.build()
}

private fun boundsPaddingPx(
    padding: PaddingValues,
    density: Density,
    layoutDirection: LayoutDirection
): Int =
    with(density) {
        maxOf(
            padding.calculateLeftPadding(layoutDirection).toPx(),
            padding.calculateTopPadding().toPx(),
            padding.calculateRightPadding(layoutDirection).toPx(),
            padding.calculateBottomPadding().toPx()
        ).toInt()
    }

private fun positionFor(
    intent: CameraIntent,
    target: GeoPoint,
    camera: CameraPositionState
): CameraPosition =
    CameraPosition
        .Builder()
        .target(LatLng(target.lat, target.lng))
        .zoom(intent.zoom ?: camera.position.zoom)
        .bearing(camera.position.bearing)
        .tilt(camera.position.tilt)
        .build()

private val CameraIntent.isBoundsIntent: Boolean get() = (bounds?.size ?: 0) >= 2

private data class GooglePaddingPx(
    val left: Float,
    val top: Float,
    val right: Float,
    val bottom: Float
)

private fun PaddingValues.toPx(
    density: Density,
    layoutDirection: LayoutDirection
): GooglePaddingPx =
    with(density) {
        GooglePaddingPx(
            left = calculateLeftPadding(layoutDirection).toPx(),
            top = calculateTopPadding().toPx(),
            right = calculateRightPadding(layoutDirection).toPx(),
            bottom = calculateBottomPadding().toPx()
        )
    }
