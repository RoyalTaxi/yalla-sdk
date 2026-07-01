package uz.yalla.carto.render.maplibre

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.maplibre.compose.expressions.dsl.const
import org.maplibre.compose.expressions.dsl.exponential
import org.maplibre.compose.expressions.dsl.interpolate
import org.maplibre.compose.expressions.dsl.zoom
import org.maplibre.compose.layers.CircleLayer
import org.maplibre.compose.sources.GeoJsonData
import org.maplibre.compose.sources.rememberGeoJsonSource
import org.maplibre.spatialk.geojson.Feature
import org.maplibre.spatialk.geojson.Point
import uz.yalla.carto.model.CartoCircle
import kotlin.math.PI
import kotlin.math.cos

private const val BASE_METERS_PER_PIXEL = 156_543.03392
private const val CIRCLE_RADIUS_MAX_ZOOM = 22
private const val CIRCLE_RADIUS_SCALE = 4_194_304f

@Composable
internal fun MapLibreCircleLayer(circle: CartoCircle) {
    val source = rememberGeoJsonSource(GeoJsonData.Features(circle.toFeature()))
    val pixelsAtZoom0 = (circle.radiusMeters / (BASE_METERS_PER_PIXEL * cos(circle.center.lat * PI / 180.0))).toFloat()
    CircleLayer(
        id = "circle-${circle.id}",
        source = source,
        color = const(Color(circle.fillArgb)),
        radius =
            interpolate(
                exponential(2f),
                zoom(),
                0 to const(pixelsAtZoom0.dp),
                CIRCLE_RADIUS_MAX_ZOOM to const((pixelsAtZoom0 * CIRCLE_RADIUS_SCALE).dp)
            ),
        strokeColor = const(Color(circle.strokeArgb)),
        strokeWidth = const(circle.strokeWidthDp.dp)
    )
}

private fun CartoCircle.toFeature(): Feature<Point, Nothing?> =
    Feature(geometry = Point(center.toPosition()), properties = null)
