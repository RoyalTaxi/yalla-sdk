package uz.yalla.maps.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import uz.yalla.maps.model.LatLng

@Stable
class MarkerState(
    position: LatLng = LatLng(0.0, 0.0)
) {
    var position: LatLng by mutableStateOf(position)
}

@Composable
fun rememberUpdatedMarkerState(position: LatLng = LatLng(0.0, 0.0)): MarkerState =
    remember { MarkerState(position) }.also { it.position = position }
