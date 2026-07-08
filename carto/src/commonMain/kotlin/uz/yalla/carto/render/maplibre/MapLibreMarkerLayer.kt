package uz.yalla.carto.render.maplibre

import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.painter.Painter
import org.maplibre.compose.expressions.dsl.const
import org.maplibre.compose.expressions.dsl.eq
import org.maplibre.compose.expressions.dsl.feature
import org.maplibre.compose.expressions.dsl.image
import org.maplibre.compose.expressions.value.FloatValue
import org.maplibre.compose.expressions.value.IconRotationAlignment
import org.maplibre.compose.expressions.value.StringValue
import org.maplibre.compose.expressions.value.SymbolAnchor
import org.maplibre.compose.layers.SymbolLayer
import uz.yalla.carto.state.MapState

@Composable
internal fun MapLibreMarkerLayer(
    state: MapState,
    icons: Map<String, Painter>
) {
    icons.forEach { (iconKey, painter) ->
        key(iconKey) {
            val store = remember(state, iconKey) { MarkerFeatureStore(iconKey) }
            val source = store.rememberSource(state)
            SymbolLayer(
                id = "markers-$iconKey",
                source = source,
                filter = feature[MapLibreKeys.ICON].cast<StringValue>() eq const(iconKey),
                iconImage = image(painter),
                iconRotate = feature[MapLibreKeys.ROTATION].cast<FloatValue>(),
                iconRotationAlignment = const(IconRotationAlignment.Map),
                iconAnchor = const(SymbolAnchor.Center),
                iconAllowOverlap = const(true),
                sortKey = feature[MapLibreKeys.SORT].cast<FloatValue>()
            )
        }
    }
}
