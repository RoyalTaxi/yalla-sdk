package uz.yalla.maps.provider.libre.component

import androidx.compose.runtime.Composable
import io.github.dellisd.spatialk.geojson.dsl.featureCollection
import io.github.dellisd.spatialk.geojson.dsl.lineString
import org.maplibre.compose.expressions.dsl.const
import org.maplibre.compose.expressions.value.LineCap
import org.maplibre.compose.expressions.value.LineJoin
import org.maplibre.compose.layers.LineLayer
import org.maplibre.compose.sources.GeoJsonData
import org.maplibre.compose.sources.rememberGeoJsonSource
import uz.yalla.core.geo.GeoPoint
import uz.yalla.maps.provider.common.MapColors
import uz.yalla.maps.provider.common.MapDimens

private const val ROUTE_LAYER_ID = "route-layer"

/**
 * MapLibre route polyline layer drawn from a list of [GeoPoint]s.
 *
 * Renders a colored line along the route path using a MapLibre [LineLayer].
 * No-ops when fewer than two points are provided.
 *
 * @param route Ordered list of coordinates defining the route path.
 * @since 0.0.1
 */
@Composable
fun RouteLayer(route: List<GeoPoint>) {
    if (route.size < 2) return

    val source =
        rememberGeoJsonSource(
            data =
                GeoJsonData.Features(
                    featureCollection {
                        feature(
                            geometry =
                                lineString {
                                    route.forEach { point ->
                                        point(longitude = point.lng, latitude = point.lat)
                                    }
                                }
                        )
                    }
                )
        )

    LineLayer(
        id = ROUTE_LAYER_ID,
        source = source,
        color = const(MapColors.Primary),
        width = const(MapDimens.RouteWidth),
        cap = const(LineCap.Round),
        join = const(LineJoin.Round)
    )
}
