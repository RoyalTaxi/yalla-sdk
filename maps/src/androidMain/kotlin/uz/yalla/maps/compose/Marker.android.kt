package uz.yalla.maps.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.geometry.Offset
import com.google.android.gms.maps.model.LatLng as GoogleLatLng
import com.google.maps.android.compose.Marker as AndroidMarker
import com.google.maps.android.compose.rememberUpdatedMarkerState as androidRememberUpdatedMarkerState

@Composable
@GoogleMapComposable
actual fun Marker(
    state: MarkerState,
    icon: BitmapDescriptor?,
    anchor: Offset,
    flat: Boolean,
    rotation: Float,
) {
    val androidState =
        androidRememberUpdatedMarkerState(
            position = GoogleLatLng(state.position.latitude, state.position.longitude)
        )

    LaunchedEffect(state.position) {
        val newPosition = GoogleLatLng(state.position.latitude, state.position.longitude)
        if (androidState.position != newPosition) {
            androidState.position = newPosition
        }
    }

    AndroidMarker(
        state = androidState,
        anchor = anchor,
        flat = flat,
        icon = icon?.googleBitmapDescriptor,
        rotation = rotation,
    )
}
