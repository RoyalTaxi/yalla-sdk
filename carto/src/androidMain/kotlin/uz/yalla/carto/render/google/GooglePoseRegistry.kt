package uz.yalla.carto.render.google

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.runtime.toMutableStateMap
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMapComposable
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import kotlinx.coroutines.flow.SharedFlow
import uz.yalla.carto.model.MapAnchor
import uz.yalla.carto.state.MarkerPose

internal class PosedMarkerState(
    point: LatLng,
    bearing: Float,
    iconKey: String?,
    anchor: MapAnchor
) {
    val markerState: MarkerState = MarkerState(point)
    val bearing = mutableFloatStateOf(bearing)
    var iconKey by mutableStateOf(iconKey)
    var anchor by mutableStateOf(anchor)

    fun update(pose: MarkerPose) {
        markerState.position = LatLng(pose.point.lat, pose.point.lng)
        bearing.floatValue = pose.bearing
        iconKey = pose.iconKey
        anchor = pose.anchor
    }
}

internal class PoseRegistry(
    private val map: SnapshotStateMap<String, PosedMarkerState>
) {
    val entries: List<Map.Entry<String, PosedMarkerState>> get() = map.entries.toList()

    fun has(id: String): Boolean = map.containsKey(id)

    fun retain(ids: Set<String>) {
        map.keys
            .toList()
            .forEach { id ->
                if (id !in ids) map.remove(id)
            }
    }

    fun apply(pose: MarkerPose) {
        val existing = map[pose.id]
        if (existing != null) existing.update(pose) else map[pose.id] = newState(pose)
    }

    private fun newState(pose: MarkerPose) =
        PosedMarkerState(LatLng(pose.point.lat, pose.point.lng), pose.bearing, pose.iconKey, pose.anchor)
}

@Composable
internal fun rememberPoseRegistry(poses: SharedFlow<MarkerPose>): PoseRegistry {
    val registry = remember { PoseRegistry(emptyList<Pair<String, PosedMarkerState>>().toMutableStateMap()) }
    LaunchedEffect(poses) { poses.collect { registry.apply(it) } }
    return registry
}

@Composable
@GoogleMapComposable
internal fun PosedMarker(
    pose: PosedMarkerState,
    scope: MarkerScope
) {
    val bearing by pose.bearing
    Marker(
        state = pose.markerState,
        rotation = bearing,
        flat = true,
        anchor = anchorOf(pose.anchor),
        icon = scope.resolve(pose.iconKey)
    )
}
