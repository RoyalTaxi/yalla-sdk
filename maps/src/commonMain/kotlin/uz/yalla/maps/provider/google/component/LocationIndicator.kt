package uz.yalla.maps.provider.google.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import uz.yalla.core.geo.GeoPoint
import uz.yalla.maps.compose.Circle
import uz.yalla.maps.compose.Marker
import uz.yalla.maps.compose.rememberComposeBitmapDescriptor
import uz.yalla.maps.compose.rememberUpdatedMarkerState
import uz.yalla.maps.provider.common.MapColors
import uz.yalla.maps.provider.common.MapDimens
import uz.yalla.maps.provider.common.UserLocationPainter
import uz.yalla.maps.provider.google.toLatLng

/**
 * Google Maps user-location indicator with accuracy circle.
 *
 * Displays a [UserLocationPainter] dot at the user's position overlaid on
 * a translucent [Circle] representing GPS accuracy.
 *
 * @param location Current user location, or `null` to hide the indicator.
 * @param accuracyMeters GPS accuracy radius in meters.
 * @since 0.0.1
 */
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

    val icon = rememberComposeBitmapDescriptor("user-location", MapDimens.UserLocationSize) {
        Canvas(modifier = Modifier.size(MapDimens.UserLocationSize)) {
            with(UserLocationPainter) { draw(size) }
        }
    }
    Marker(
        state = rememberUpdatedMarkerState(position = location.toLatLng()),
        icon = icon,
        anchor = Offset(0.5f, 0.5f)
    )
}
