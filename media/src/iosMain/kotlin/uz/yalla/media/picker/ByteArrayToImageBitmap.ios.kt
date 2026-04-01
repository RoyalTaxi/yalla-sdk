package uz.yalla.media.picker

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import org.jetbrains.skia.Image

/**
 * iOS implementation of [ByteArray.toImageBitmap].
 *
 * Decodes raw image bytes via Skia's [Image.makeFromEncoded] and converts the result
 * to a Compose [ImageBitmap] using `toComposeImageBitmap`.
 *
 * @since 0.0.1
 */
actual fun ByteArray.toImageBitmap(): ImageBitmap = Image.makeFromEncoded(this).toComposeImageBitmap()
