package uz.yalla.maps.compose

import androidx.compose.ui.graphics.ImageBitmap

/**
 * iOS implementation: converts an [ImageBitmap] to a [BitmapDescriptor].
 *
 * Delegates to [toUIImage] which handles pixel format conversion and
 * proper device-screen scale.
 *
 * @return A [BitmapDescriptor] wrapping the converted `UIImage`.
 * @since 0.0.1
 */
internal actual fun ImageBitmap.toBitmapDescriptor(): BitmapDescriptor {
    return BitmapDescriptor(this.toUIImage())
}
