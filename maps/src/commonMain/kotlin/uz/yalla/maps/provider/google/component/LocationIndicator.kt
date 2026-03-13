package uz.yalla.maps.provider.google.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import uz.yalla.core.geo.GeoPoint
import uz.yalla.maps.compose.Circle
import uz.yalla.maps.compose.Marker
import uz.yalla.maps.compose.rememberComposeBitmapDescriptor
import uz.yalla.maps.compose.rememberUpdatedMarkerState
import uz.yalla.maps.provider.common.MapColors
import uz.yalla.maps.provider.common.MapDimens
import uz.yalla.maps.provider.common.UserLocationPainter
import uz.yalla.maps.provider.google.toLatLng

@Composable
fun LocationIndicator(
    location: GeoPoint?,
    accuracyMeters: Double = MapDimens.DEFAULT_ACCURACY_METERS
) {
    location ?: return

    Circle(
        center = location.toLatLng(),
        radius = accuracyMeters,
        fillColor = MapColors.Primary.copy(alpha = 0.2f),
        strokeColor = MapColors.Primary.copy(alpha = 0.4f),
        strokeWidth = 1f
    )

    val icon = rememberComposeBitmapDescriptor("user-location") {
        Canvas(modifier = Modifier.size(15.dp)) {
            with(UserLocationPainter) { draw(size) }
        }
    }
    Marker(
        state = rememberUpdatedMarkerState(position = location.toLatLng()),
        icon = icon,
        anchor = Offset(0.5f, 0.5f)
    )
}
