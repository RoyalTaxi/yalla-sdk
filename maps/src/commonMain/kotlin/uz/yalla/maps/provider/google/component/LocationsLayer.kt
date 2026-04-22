package uz.yalla.maps.provider.google.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import uz.yalla.core.geo.GeoPoint
import uz.yalla.maps.compose.Marker
import uz.yalla.maps.compose.rememberComposeBitmapDescriptor
import uz.yalla.maps.compose.rememberUpdatedMarkerState
import uz.yalla.maps.provider.common.LocationType
import uz.yalla.maps.provider.common.MapColors
import uz.yalla.maps.provider.common.MapDimens
import uz.yalla.maps.provider.google.toLatLng

/**
 * Google Maps marker layer rendering location markers for start, intermediate, and finish points.
 *
 * Each marker is a colored circle rendered via [rememberComposeBitmapDescriptor] with
 * an optional text badge. Markers are placed as Google Maps [Marker] overlays.
 *
 * @param arrival Arrival time hint (currently unused, reserved for future badge content).
 * @param duration Duration hint (currently unused, reserved for future badge content).
 * @param locations Ordered list of geographic coordinates for the markers.
 * @param startLabel Optional text label for the first (start) marker.
 * @param endLabel Optional text label for the last (finish) marker.
 * @since 0.0.1
 */
@Composable
@Suppress("UnusedParameter") // arrival/duration retained for API parity with MapLibre provider (Phase 5)
fun LocationsLayer(
    arrival: Int?,
    duration: Int?,
    locations: List<GeoPoint>,
    startLabel: String? = null,
    endLabel: String? = null
) {
    if (locations.isEmpty()) return

    locations.forEachIndexed { index, location ->
        val type =
            when (index) {
                0 -> LocationType.START
                locations.lastIndex -> LocationType.FINISH
                else -> LocationType.POINT
            }

        val badgeText =
            when (index) {
                0 -> startLabel
                locations.lastIndex -> endLabel
                else -> null
            }

        key("location-${location.lat}-${location.lng}") {
            LocationMarker(
                location = location,
                type = type,
                badgeText = badgeText
            )
        }
    }
}

@Composable
private fun LocationMarker(
    location: GeoPoint,
    type: LocationType,
    badgeText: String?
) {
    val icon =
        rememberComposeBitmapDescriptor(type, badgeText.orEmpty()) {
            LocationMarkerContent(type = type, badgeText = badgeText)
        }

    Marker(
        state = rememberUpdatedMarkerState(position = location.toLatLng()),
        icon = icon,
        anchor = Offset(0.5f, 0.5f)
    )
}

@Composable
private fun LocationMarkerContent(
    type: LocationType,
    badgeText: String?
) {
    val borderColor =
        when (type) {
            LocationType.START -> MapColors.Primary
            LocationType.POINT -> MapColors.IntermediateMarker
            LocationType.FINISH -> MapColors.FinishMarker
        }
    val density = LocalDensity.current
    val borderWidthPx = with(density) { MapDimens.MarkerBorderWidth.toPx() }

    Column(
        modifier = Modifier.wrapContentSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (badgeText != null) {
            Box(
                modifier =
                    Modifier
                        .height(MapDimens.BadgeHeight)
                        .background(
                            color = MapColors.BadgeBackground,
                            shape = RoundedCornerShape(MapDimens.BadgeHeight / 2)
                        ).padding(horizontal = MapDimens.BadgePadding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = badgeText,
                    style =
                        TextStyle(
                            color = MapColors.BadgeText,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                )
            }
        }

        Canvas(
            modifier = Modifier.size(MapDimens.MarkerSize)
        ) {
            val radius = size.minDimension / 2f
            drawCircle(color = borderColor, radius = radius)
            val innerRadius = (radius - borderWidthPx).coerceAtLeast(0f)
            if (innerRadius > 0f) {
                drawCircle(color = MapColors.MarkerFill, radius = innerRadius)
            }
        }

        if (badgeText != null) {
            Spacer(modifier = Modifier.height(MapDimens.BadgeHeight))
        }
    }
}
