package uz.yalla.carto.render.maplibre

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.maplibre.compose.expressions.dsl.const
import org.maplibre.compose.expressions.dsl.nil
import org.maplibre.compose.expressions.value.LineCap
import org.maplibre.compose.expressions.value.LineJoin
import org.maplibre.compose.layers.LineLayer
import org.maplibre.compose.sources.GeoJsonData
import org.maplibre.compose.sources.rememberGeoJsonSource
import org.maplibre.spatialk.geojson.Feature
import org.maplibre.spatialk.geojson.LineString
import uz.yalla.carto.model.CartoRoute
import uz.yalla.carto.model.LinePattern

@Composable
internal fun MapLibreLineLayer(route: CartoRoute) {
    val source = rememberGeoJsonSource(GeoJsonData.Features(route.toFeature()))
    LineLayer(
        id = "line-${route.id}",
        source = source,
        color = const(Color(route.colorArgb)),
        width = const(route.widthDp.dp),
        cap = const(LineCap.Round),
        join = const(LineJoin.Round),
        dasharray = if (route.pattern == LinePattern.DASHED) const(listOf<Number>(2, 4)) else nil()
    )
}

private fun CartoRoute.toFeature(): Feature<LineString, Nothing?> =
    Feature(geometry = LineString(points.map { it.toPosition() }), properties = null)
