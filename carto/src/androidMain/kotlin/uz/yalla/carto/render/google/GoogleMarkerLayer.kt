package uz.yalla.carto.render.google

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.painter.Painter
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMapComposable
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberUpdatedMarkerState
import kotlinx.coroutines.flow.StateFlow
import uz.yalla.carto.model.CartoMarker
import uz.yalla.carto.state.MapState

internal class MarkerScope(
    val poses: PoseRegistry,
    val resolve: (String?) -> BitmapDescriptor?
)

@Composable
@GoogleMapComposable
internal fun GoogleMarkerLayer(
    state: MapState,
    icons: Map<String, Painter>
) {
    val scope = MarkerScope(rememberPoseRegistry(state.poses), rememberIconResolver(icons))
    state.markerSources.forEach { source -> StaticMarkers(source, scope) }
    scope.poses.entries.forEach { (_, pose) -> PosedMarker(pose, scope) }
}

@Composable
@GoogleMapComposable
private fun StaticMarkers(
    source: StateFlow<List<CartoMarker>>,
    scope: MarkerScope
) {
    val markers by source.collectAsStateWithLifecycle()
    markers.forEach { marker -> if (!scope.poses.has(marker.id)) StaticMarker(marker, scope) }
}

@Composable
@GoogleMapComposable
private fun StaticMarker(
    marker: CartoMarker,
    scope: MarkerScope
) {
    Marker(
        state = rememberUpdatedMarkerState(LatLng(marker.point.lat, marker.point.lng)),
        icon = scope.resolve(marker.iconKey),
        anchor = anchorOf(marker.anchor),
        rotation = marker.rotation,
        flat = marker.flat,
        zIndex = marker.zBand.zIndex() + marker.zIndex
    )
}
