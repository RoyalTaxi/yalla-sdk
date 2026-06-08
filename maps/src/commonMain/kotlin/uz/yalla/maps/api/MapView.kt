package uz.yalla.maps.api

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import uz.yalla.core.settings.MapKind
import uz.yalla.core.settings.ThemeKind
import uz.yalla.maps.api.model.CameraPosition
import uz.yalla.maps.api.model.MapCircle
import uz.yalla.maps.api.model.MapMarker
import uz.yalla.maps.api.model.MapRoute
import uz.yalla.maps.api.model.MapStyle
import uz.yalla.maps.config.YallaMaps

@Composable
fun rememberMapController(
    overrideKind: MapKind? = null,
    style: MapStyle = MapStyle.CARTO,
    initialPosition: CameraPosition? = null
): MapController {
    val config = remember { YallaMaps.current() }
    val factory = remember { config.factory }
    val kindFlow = remember(overrideKind) {
        if (overrideKind != null) kotlinx.coroutines.flow.flowOf(overrideKind) else config.mapKindPreference
    }
    val themeFlow = remember { config.themePreference }
    val kind by kindFlow.collectAsState(initial = MapKind.Libre)
    val theme by themeFlow.collectAsState(initial = ThemeKind.System)
    val systemDark = isSystemInDarkTheme()
    val isDark = when (theme) {
        ThemeKind.Light -> false
        ThemeKind.Dark -> true
        ThemeKind.System -> systemDark
    }

    val switching = remember { SwitchingMapController(factory, initialPosition) }

    LaunchedEffect(kind) {
        switching.switchTo(kind)
        switching.applyStyle(style, isDark)
    }

    LaunchedEffect(style, isDark) {
        switching.applyStyle(style, isDark)
    }

    DisposableEffect(switching) {
        onDispose { switching.close() }
    }

    return switching
}

@Composable
fun MapView(
    controller: MapController,
    modifier: Modifier = Modifier,
    markers: List<MapMarker> = emptyList(),
    routes: List<MapRoute> = emptyList(),
    circles: List<MapCircle> = emptyList(),
    contentPadding: PaddingValues = PaddingValues(),
    onReady: (() -> Unit)? = null
) {
    LaunchedEffect(controller, contentPadding) { controller.setDesiredPadding(contentPadding) }
    LaunchedEffect(controller, markers) { controller.setMarkers(markers) }
    LaunchedEffect(controller, routes) { controller.setRoutes(routes) }
    LaunchedEffect(controller, circles) { controller.setCircles(circles) }

    val ready by controller.isReady.collectAsState()
    LaunchedEffect(ready, onReady) {
        if (ready && onReady != null) onReady()
    }

    val hosted: MapController? = if (controller is SwitchingMapController) {
        controller.activeBackend.collectAsState().value
    } else controller

    if (hosted != null) {
        key(hosted) {
            MapHost(controller = hosted, modifier = modifier)
        }
    }
}

@Composable
fun StaticMapView(
    points: List<uz.yalla.core.geo.GeoPoint>,
    modifier: Modifier = Modifier,
    routes: List<MapRoute> = emptyList(),
    markers: List<MapMarker> = emptyList(),
    contentPadding: PaddingValues = PaddingValues()
) {
    val controller = rememberMapController()
    MapView(
        controller = controller,
        modifier = modifier,
        markers = markers,
        routes = routes,
        contentPadding = contentPadding,
        onReady = null
    )
    val ready by controller.isReady.collectAsState()
    LaunchedEffect(ready, points) {
        if (ready && points.isNotEmpty()) controller.fitBounds(points, animate = false)
    }
}
