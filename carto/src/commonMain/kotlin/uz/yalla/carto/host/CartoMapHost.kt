package uz.yalla.carto.host

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import uz.yalla.carto.CartoMap

@Composable
public expect fun CartoMapHost(
    map: CartoMap,
    icons: Map<String, Painter> = emptyMap(),
    modifier: Modifier = Modifier
)
