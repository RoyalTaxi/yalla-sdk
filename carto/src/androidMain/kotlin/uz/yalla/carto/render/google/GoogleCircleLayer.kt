package uz.yalla.carto.render.google

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.GoogleMapComposable
import kotlinx.coroutines.flow.StateFlow
import uz.yalla.carto.model.CartoCircle
import uz.yalla.carto.model.ZBand

@Composable
@GoogleMapComposable
internal fun GoogleCircleLayer(sources: List<StateFlow<List<CartoCircle>>>) {
    sources.forEach { source ->
        val circles by source.collectAsStateWithLifecycle()
        circles.forEach { circle -> GoogleCircle(circle) }
    }
}

@Composable
@GoogleMapComposable
private fun GoogleCircle(circle: CartoCircle) {
    Circle(
        center = LatLng(circle.center.lat, circle.center.lng),
        radius = circle.radiusMeters,
        fillColor = Color(circle.fillArgb),
        strokeColor = Color(circle.strokeArgb),
        strokeWidth = circle.strokeWidthDp,
        zIndex = circle.zBand.zIndex()
    )
}

internal fun ZBand.zIndex(): Float = ordinal.toFloat() * 1000f
