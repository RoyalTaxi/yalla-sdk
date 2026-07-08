package uz.yalla.carto.render.google

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.graphics.painter.Painter
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMapComposable
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberUpdatedMarkerState
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
    val markerLists = state.markerSources.map { source -> source.collectAsStateWithLifecycle().value }
    val activeMarkerIds = markerLists.flatMap { markers -> markers.map { it.id } }.toSet()
    LaunchedEffect(activeMarkerIds) { scope.poses.retain(activeMarkerIds) }
    markerLists.forEach { markers -> StaticMarkers(markers, scope) }
    scope.poses.entries.forEach { (id, pose) ->
        if (id in activeMarkerIds) key(id) { PosedMarker(pose, scope) }
    }
}

@Composable
@GoogleMapComposable
private fun StaticMarkers(
    markers: List<CartoMarker>,
    scope: MarkerScope
) {
    markers.forEach { marker ->
        key(marker.id) {
            if (!scope.poses.has(marker.id)) StaticMarker(marker, scope)
        }
    }
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
