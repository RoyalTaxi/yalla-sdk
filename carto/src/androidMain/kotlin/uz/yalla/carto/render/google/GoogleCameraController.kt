package uz.yalla.carto.render.google

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.CameraPositionState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import uz.yalla.carto.camera.CameraIntent
import uz.yalla.carto.camera.CameraView
import uz.yalla.core.geo.GeoPoint

@Composable
internal fun GoogleCameraController(
    cameraPositionState: CameraPositionState,
    intents: SharedFlow<CameraIntent>,
    ready: StateFlow<Boolean>
) {
    val density = LocalDensity.current
    val layoutDirection = LocalLayoutDirection.current
    LaunchedEffect(cameraPositionState, intents, ready, density, layoutDirection) {
        var job: Job? = null
        intents.collect { intent ->
            job?.cancel()
            job =
                launch {
                    ready.first { it }
                    val update = updateFor(intent, cameraPositionState, density, layoutDirection) ?: return@launch
                    val boundsFit = (intent.bounds?.size ?: 0) >= 2
                    runCatching {
                        if (intent.animate) {
                            cameraPositionState.animate(update, intent.durationMs)
                        } else {
                            cameraPositionState.move(update)
                        }
                        if (boundsFit && cameraPositionState.position.zoom > CameraView.FIT_ZOOM_MAX) {
                            cameraPositionState.move(CameraUpdateFactory.zoomTo(CameraView.FIT_ZOOM_MAX))
                        }
                    }
                }
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
