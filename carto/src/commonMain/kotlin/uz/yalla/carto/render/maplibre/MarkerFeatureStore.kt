package uz.yalla.carto.render.maplibre

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.maplibre.compose.sources.GeoJsonData
import org.maplibre.compose.sources.GeoJsonSource
import org.maplibre.compose.sources.rememberGeoJsonSource
import org.maplibre.spatialk.geojson.Feature
import org.maplibre.spatialk.geojson.FeatureCollection
import org.maplibre.spatialk.geojson.Point
import uz.yalla.carto.model.CartoMarker
import uz.yalla.carto.state.MapState
import uz.yalla.carto.state.MarkerPose

internal class MarkerFeatureStore {
    private val features = linkedMapOf<String, Feature<Point, JsonObject>>()
    private val posed = hashSetOf<String>()

    @Composable
    fun rememberSource(state: MapState): GeoJsonSource {
        val source = rememberGeoJsonSource(GeoJsonData.Features(FeatureCollection<Point, JsonObject>()))
        LaunchedEffect(state, source) { observe(state, source) }
        return source
    }

    private suspend fun observe(
        state: MapState,
        source: GeoJsonSource
    ) = coroutineScope {
        launch { collectMarkers(state, source) }
        launch { state.poses.collect { pose -> applyPose(pose, source) } }
    }

    private suspend fun collectMarkers(
        state: MapState,
        source: GeoJsonSource
    ) {
        mergedMarkers(state).collect { markers ->
            val incoming = markers.associateBy { it.id }
            (features.keys - incoming.keys).toList().forEach {
                features.remove(it)
                posed.remove(it)
            }
            incoming.forEach { (id, marker) -> if (id !in posed) features[id] = marker.toFeature() }
            push(source)
        }
    }

    private fun applyPose(
        pose: MarkerPose,
        source: GeoJsonSource
    ) {
        val current = features[pose.id] ?: return
        posed.add(pose.id)
        features[pose.id] = current.movedTo(pose)
        push(source)
    }

    private fun push(source: GeoJsonSource) {
        source.setData(GeoJsonData.Features(FeatureCollection(features.values.toList())))
    }
}

private fun mergedMarkers(state: MapState): Flow<List<CartoMarker>> =
    if (state.markerSources.isEmpty()) {
        flowOf(emptyList())
    } else {
        combine(state.markerSources) { lists -> lists.toList().flatMap { it } }
    }

private fun CartoMarker.toFeature(): Feature<Point, JsonObject> =
    Feature(Point(point.toPosition()), markerProperties(id, iconKey ?: "", rotation, sortKey()))

private fun CartoMarker.sortKey(): Float = zBand.ordinal * 1000f + zIndex

private fun Feature<Point, JsonObject>.movedTo(pose: MarkerPose): Feature<Point, JsonObject> =
    Feature(Point(pose.point.toPosition()), markerProperties(pose.id, iconOf(), pose.bearing, sortOf()))

private fun Feature<Point, JsonObject>.iconOf(): String =
    (properties[MapLibreKeys.ICON] as? JsonPrimitive)?.content ?: ""

private fun Feature<Point, JsonObject>.sortOf(): Float =
    (properties[MapLibreKeys.SORT] as? JsonPrimitive)?.content?.toFloatOrNull() ?: 0f

private fun markerProperties(
    id: String,
    icon: String,
    rotation: Float,
    sort: Float
): JsonObject =
    buildJsonObject {
        put(MapLibreKeys.ID, id)
        put(MapLibreKeys.ICON, icon)
        put(MapLibreKeys.ROTATION, rotation)
        put(MapLibreKeys.SORT, sort)
    }
