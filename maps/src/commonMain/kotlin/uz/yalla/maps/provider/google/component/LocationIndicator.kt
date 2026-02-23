package uz.yalla.maps.provider.google.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import uz.yalla.core.geo.GeoPoint
import uz.yalla.maps.compose.Circle
import uz.yalla.maps.compose.Marker
import uz.yalla.maps.compose.rememberBitmapDescriptor
import uz.yalla.maps.compose.rememberUpdatedMarkerState
import uz.yalla.maps.provider.common.MapColors
import uz.yalla.maps.provider.common.MapDimens
import uz.yalla.maps.provider.google.toLatLng
import uz.yalla.resources.Res
import uz.yalla.resources.ic_user_location

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

    val icon = rememberBitmapDescriptor(Res.drawable.ic_user_location)
    Marker(
        state = rememberUpdatedMarkerState(position = location.toLatLng()),
        icon = icon,
        anchor = Offset(0.5f, 0.5f)
    )
}
