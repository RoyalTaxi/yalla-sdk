package uz.yalla.maps.compose

import androidx.compose.runtime.Composable

/**
 * Captures a Compose composable snapshot and returns it as a [BitmapDescriptor].
 *
 * Renders the [content] composable off-screen, rasterizes it to a bitmap, and wraps
 * the result in a [BitmapDescriptor] for use as a marker icon. The result is
 * recomputed when any of the [keys] change.
 *
 * @param keys Cache invalidation keys; the descriptor is recaptured when keys change.
 * @param content The composable content to rasterize.
 * @return A [BitmapDescriptor] containing the rendered content.
 * @since 0.0.1
 */
@Composable
@GoogleMapComposable
expect fun rememberComposeBitmapDescriptor(
    vararg keys: Any,
    content: @Composable () -> Unit,
): BitmapDescriptor
