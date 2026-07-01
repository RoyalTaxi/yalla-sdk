package uz.yalla.carto.host

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import uz.yalla.carto.CartoProvider
import uz.yalla.carto.camera.CameraIntent
import uz.yalla.carto.camera.CameraView
import uz.yalla.carto.capability.circles
import uz.yalla.carto.capability.lines
import uz.yalla.carto.capability.markers
import uz.yalla.carto.model.CartoCircle
import uz.yalla.carto.model.CartoMarker
import uz.yalla.carto.model.CartoRoute
import uz.yalla.carto.state.MapStyle
import uz.yalla.core.geo.GeoPoint

@Composable
public fun StaticMapView(
    provider: CartoProvider,
    points: List<GeoPoint>,
    modifier: Modifier = Modifier,
    isDark: Boolean = false,
    style: MapStyle = MapStyle.Carto,
    lines: List<CartoRoute> = emptyList(),
    markers: List<CartoMarker> = emptyList(),
    circles: List<CartoCircle> = emptyList(),
    icons: Map<String, Painter> = emptyMap(),
    contentPadding: PaddingValues = PaddingValues()
) {
    val map =
        rememberCartoMap(
            provider = provider,
            style = style,
            isDark = isDark,
            clippable = true
        ) {
            lines()
            markers()
            circles()
        }

    LaunchedEffect(map, lines) { map.lines.set(lines) }
    LaunchedEffect(map, markers) { map.markers.set(markers) }
    LaunchedEffect(map, circles) { map.circles.set(circles) }

    val ready by map.isReady.collectAsStateWithLifecycle()
    LaunchedEffect(ready, points, lines, contentPadding) {
        if (!ready) return@LaunchedEffect
        val framed =
            (points + lines.flatMap { it.points })
                .filterNot { it == GeoPoint.Zero }
                .distinctBy { it.lat to it.lng }
        when {
            framed.size > 1 ->
                map.arbiter.submit(
                    CameraIntent(
                        bounds = framed,
                        boundsPadding = contentPadding,
                        animate = false
                    )
                )

            framed.size == 1 ->
                map.arbiter.submit(
                    CameraIntent(
                        target = framed.first(),
                        zoom = CameraView.DEFAULT_ZOOM,
                        animate = false
                    )
                )
        }
    }

    Box(modifier = modifier) {
        CartoMapHost(
            map = map,
            icons = icons,
            modifier = Modifier.matchParentSize()
        )

        Box(
            modifier =
                Modifier
                    .matchParentSize()
                    .pointerInput(Unit) {
                        awaitPointerEventScope {
                            while (true) {
                                awaitPointerEvent().changes.forEach { it.consume() }
                            }
                        }
                    }
        )
    }
}
