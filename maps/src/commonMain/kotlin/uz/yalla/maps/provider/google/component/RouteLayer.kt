package uz.yalla.maps.provider.google.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import uz.yalla.core.geo.GeoPoint
import uz.yalla.maps.compose.Polyline
import uz.yalla.maps.model.Cap
import uz.yalla.maps.model.JointType
import uz.yalla.maps.provider.common.MapColors
import uz.yalla.maps.provider.common.MapDimens
import uz.yalla.maps.provider.google.toLatLng

@Composable
fun RouteLayer(route: List<GeoPoint>) {
    if (route.size < 2) return

    val widthPx = with(LocalDensity.current) { MapDimens.RouteWidth.toPx() }

    Polyline(
        points = route.map { it.toLatLng() },
        color = MapColors.Primary,
        width = widthPx,
        jointType = JointType.Round,
        startCap = Cap.Round,
        endCap = Cap.Round
    )
}
