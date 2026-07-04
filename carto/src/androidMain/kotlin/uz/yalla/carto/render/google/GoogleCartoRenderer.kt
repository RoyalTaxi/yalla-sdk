package uz.yalla.carto.render.google

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.flow.MutableStateFlow
import uz.yalla.carto.state.MapEvent
import uz.yalla.carto.state.MapState
import uz.yalla.core.geo.GeoPoint

@Composable
public fun GoogleCartoRenderer(
    state: MapState,
    icons: Map<String, Painter>,
    modifier: Modifier
) {
    val camera =
        rememberCameraPositionState {
            position =
                CameraPosition.fromLatLngZoom(
                    LatLng(state.initialCamera.target.lat, state.initialCamera.target.lng),
                    state.initialCamera.zoom
                )
        }
    val ready = remember { MutableStateFlow(false) }
    val isDark by state.isDark.collectAsStateWithLifecycle()
    val style by state.style.collectAsStateWithLifecycle()
    val padding by state.padding.collectAsStateWithLifecycle()
    GoogleCameraController(camera, state.cameraIntents, ready, padding)
    GoogleCameraReporter(camera, state)
    DisposableEffect(state) { onDispose { state.reportReady(false) } }
    GoogleMap(
        modifier = modifier,
        cameraPositionState = camera,
        properties = mapPropertiesOf(style, isDark),
        uiSettings =
            MapUiSettings(
                compassEnabled = false,
                mapToolbarEnabled = false,
                rotationGesturesEnabled = false,
                tiltGesturesEnabled = false,
                scrollGesturesEnabled = true,
                zoomGesturesEnabled = true,
                zoomControlsEnabled = false
            ),
        mapColorScheme = colorSchemeOf(isDark),
        contentPadding = padding,
        onMapClick = { state.report(MapEvent.MapTapped(GeoPoint(it.latitude, it.longitude))) },
        onMapLongClick = { state.report(MapEvent.MapLongPressed(GeoPoint(it.latitude, it.longitude))) },
        onMapLoaded = {
            state.reportReady(true)
            ready.value = true
        }
    ) {
        GoogleCircleLayer(state.circleSources)
        GoogleLineLayer(state.lineSources)
        GoogleMarkerLayer(state, icons)
    }
}
