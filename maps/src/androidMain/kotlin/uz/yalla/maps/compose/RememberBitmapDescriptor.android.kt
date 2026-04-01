package uz.yalla.maps.compose

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import com.google.android.gms.maps.model.BitmapDescriptorFactory as GoogleBitmapDescriptorFactory

/**
 * Android implementation: converts an [ImageBitmap] to a [BitmapDescriptor].
 *
 * Extracts the underlying Android [Bitmap][android.graphics.Bitmap] and wraps it via
 * `GoogleBitmapDescriptorFactory.fromBitmap`.
 *
 * @return A [BitmapDescriptor] wrapping the converted bitmap.
 * @since 0.0.1
 */
internal actual fun ImageBitmap.toBitmapDescriptor(): BitmapDescriptor =
    BitmapDescriptor(GoogleBitmapDescriptorFactory.fromBitmap(asAndroidBitmap()))
