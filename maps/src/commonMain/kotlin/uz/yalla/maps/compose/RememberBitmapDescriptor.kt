package uz.yalla.maps.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.imageResource

/**
 * Remembers a [BitmapDescriptor] created from a Compose Multiplatform [DrawableResource].
 *
 * Loads the resource as an [ImageBitmap] and converts it to the platform bitmap handle.
 * The result is cached across recompositions as long as the underlying image is stable.
 *
 * @param resource The drawable resource to convert.
 * @return A remembered [BitmapDescriptor] suitable for use as a marker icon.
 * @since 0.0.1
 */
@OptIn(ExperimentalResourceApi::class)
@Composable
fun rememberBitmapDescriptor(resource: DrawableResource): BitmapDescriptor {
    val imageBitmap: ImageBitmap = imageResource(resource)
    return remember(imageBitmap) { imageBitmap.toBitmapDescriptor() }
}

/**
 * Converts this [ImageBitmap] to a platform-specific [BitmapDescriptor].
 *
 * On Android, extracts the `android.graphics.Bitmap` via `asAndroidBitmap`;
 * on iOS, encodes to PNG and creates a `UIImage` at the device screen scale.
 *
 * @return A [BitmapDescriptor] wrapping the converted image.
 * @since 0.0.1
 */
internal expect fun ImageBitmap.toBitmapDescriptor(): BitmapDescriptor
