package uz.yalla.maps.compose

import androidx.compose.runtime.Composable

@Composable
@GoogleMapComposable
expect fun rememberComposeBitmapDescriptor(
    vararg keys: Any,
    content: @Composable () -> Unit,
): BitmapDescriptor
