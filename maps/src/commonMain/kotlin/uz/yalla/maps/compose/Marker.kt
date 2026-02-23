package uz.yalla.maps.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset

@Composable
@GoogleMapComposable
expect fun Marker(
    state: MarkerState = rememberUpdatedMarkerState(),
    icon: BitmapDescriptor? = null,
    anchor: Offset = Offset(0.5f, 1.0f),
    flat: Boolean = false,
    rotation: Float = 0.0f,
)
