package uz.yalla.carto.host

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import uz.yalla.carto.CartoMap
import uz.yalla.carto.render.maplibre.MapLibreCartoRenderer

@Composable
public actual fun CartoMapHost(
    map: CartoMap,
    icons: Map<String, Painter>,
    modifier: Modifier
) {
    FrameDriver(map.state)
    MapLibreCartoRenderer(map.state, icons, modifier)
}
