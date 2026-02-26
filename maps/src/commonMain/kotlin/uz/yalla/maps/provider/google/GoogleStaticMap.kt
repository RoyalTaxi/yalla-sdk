package uz.yalla.maps.provider.google

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.koinInject
import uz.yalla.core.geo.GeoPoint
import uz.yalla.core.kind.ThemeKind
import uz.yalla.maps.api.StaticMap
import uz.yalla.maps.compose.rememberCameraPositionState
import uz.yalla.maps.config.MapConstants
import uz.yalla.maps.di.MapDependencies
import uz.yalla.maps.model.CameraPosition
import uz.yalla.maps.provider.google.component.LocationsLayer
import uz.yalla.maps.provider.google.component.RouteLayer
import uz.yalla.maps.util.toGeoPoint

class GoogleStaticMap : StaticMap {
    @Composable
    override fun Content(
        modifier: Modifier,
        route: List<GeoPoint>?,
        locations: List<GeoPoint>?,
        startLabel: String?,
        endLabel: String?,
        onMapReady: (() -> Unit)?
    ) {
        val dependencies: MapDependencies = koinInject()
        val density = LocalDensity.current

        val themeType by dependencies.interfacePreferences.themeType.collectAsStateWithLifecycle(ThemeKind.System)

        val fallback = MapConstants.BOBUR_SQUARE.toGeoPoint()

        val cameraState = rememberCameraPositionState {
            position = CameraPosition(
                target = fallback.toLatLng(),
                zoom = MapConstants.DEFAULT_ZOOM.toFloat()
            )
        }

        var isMapReady by remember { mutableStateOf(false) }

        val allPoints = remember(route, locations) {
            buildList {
                route?.let { addAll(it) }
                locations?.let { addAll(it) }
            }.filter { it != GeoPoint.Zero }
        }

        val paddingPx = with(density) { MapConstants.DEFAULT_PADDING.roundToPx() }

        LaunchedEffect(isMapReady, allPoints) {
            if (!isMapReady || allPoints.isEmpty()) return@LaunchedEffect

            if (allPoints.size == 1) {
                cameraState.position = CameraPosition(
                    target = allPoints.first().toLatLng(),
                    zoom = MapConstants.DEFAULT_ZOOM.toFloat()
                )
            } else {
                val bounds = allPoints.toLatLngBounds() ?: return@LaunchedEffect
                cameraState.animateToBounds(bounds, paddingPx)
            }
        }

        BaseMapContent(
            cameraState = cameraState,
            theme = themeType,
            gesturesEnabled = false,
            modifier = modifier,
            contentPadding = PaddingValues(),
            onMapReady = {
                isMapReady = true
                onMapReady?.invoke()
            }
        ) {
            if (route != null) {
                RouteLayer(route)
            }

            if (locations != null && locations.size >= 2) {
                LocationsLayer(
                    arrival = null,
                    duration = null,
                    locations = locations,
                    startLabel = startLabel,
                    endLabel = endLabel
                )
            }
        }
    }
}
