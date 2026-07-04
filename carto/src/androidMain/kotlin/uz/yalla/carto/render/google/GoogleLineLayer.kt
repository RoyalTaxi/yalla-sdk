package uz.yalla.carto.render.google

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.maps.model.Dash
import com.google.android.gms.maps.model.Gap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PatternItem
import com.google.android.gms.maps.model.RoundCap
import com.google.maps.android.compose.GoogleMapComposable
import com.google.maps.android.compose.Polyline
import kotlinx.coroutines.flow.StateFlow
import uz.yalla.carto.model.CartoRoute
import uz.yalla.carto.model.LinePattern

@Composable
@GoogleMapComposable
internal fun GoogleLineLayer(sources: List<StateFlow<List<CartoRoute>>>) {
    sources.forEach { source ->
        val routes by source.collectAsStateWithLifecycle()
        routes.forEach { route -> GoogleLine(route) }
    }
}

@Composable
@GoogleMapComposable
private fun GoogleLine(route: CartoRoute) {
    Polyline(
        points = route.points.map { LatLng(it.lat, it.lng) },
        color = Color(route.colorArgb),
        width = with(LocalDensity.current) { route.widthDp.dp.toPx() },
        pattern = dashPattern(route.pattern),
        startCap = RoundCap(),
        endCap = RoundCap(),
        zIndex = route.zBand.zIndex()
    )
}

private fun dashPattern(pattern: LinePattern): List<PatternItem>? =
    when (pattern) {
        LinePattern.SOLID -> null
        LinePattern.DASHED -> listOf(Dash(30f), Gap(20f))
    }
